package gr.uoa.di.ships.services.implementation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.vessel.VesselHistoryData;
import gr.uoa.di.ships.services.interfaces.CollisionWarningService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import gr.uoa.di.ships.utils.validators.MathUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class CollisionWarningServiceImpl implements CollisionWarningService {

  private static final double SAFE_DISTANCE_M = 1000; // “radius” at CPA (Closest Point of Approach)
  private static final double TIME_HORIZON_S  = 30 * 60; // 30 min look-ahead

  private final VesselHistoryDataService vesselHistoryDataService;

  public CollisionWarningServiceImpl(VesselHistoryDataService vesselHistoryDataService) {
    this.vesselHistoryDataService = vesselHistoryDataService;
  }

  @Override
  public List<String> collisionWarningWithVessels(RegisteredUser user, ObjectNode jsonNodeToBeSent, VesselHistoryData previousVesselData) {
    List<String> vesselsMmsisWithCollisionWarning = new ArrayList<>();
    VesselInfoForAlarm vesselInfoForAlarm = getVesselAlarmInfo(jsonNodeToBeSent);
    if (!user.getZoneOfInterestOptions().isCollisionMonitoring()
        || !vesselIsInsideZoneOfInterest(user, vesselInfoForAlarm.lat, vesselInfoForAlarm.lon)) {
      return vesselsMmsisWithCollisionWarning;
    }

    //todo: add to this logic
    vesselHistoryDataService.getLastVesselHistoryData().stream()
        .filter(historyData -> vesselIsInsideZoneOfInterest(user, historyData.getLatitude(), historyData.getLongitude()))
        .forEach(otherVesselHistoryData -> {
          addWarningIfCollisionDanger(otherVesselHistoryData, vesselInfoForAlarm, vesselsMmsisWithCollisionWarning);
        });
    return vesselsMmsisWithCollisionWarning;
  }

  private static boolean vesselIsInsideZoneOfInterest(RegisteredUser user, double lat, double lon) {
    return user.getZoneOfInterest().getRadius() >= getVesselHaversineDistanceWithZoneOfInterestCenter(user, lat, lon);
  }

  private void addWarningIfCollisionDanger(VesselHistoryData otherVesselHistoryData,
                                           VesselInfoForAlarm vesselInfoForAlarm,
                                           List<String> vesselsMmsisWithCollisionWarning) {
    if (verifyDifferentVessels(vesselInfoForAlarm.mmsi, otherVesselHistoryData)
        && validDistanceBetweenVessels(otherVesselHistoryData, vesselInfoForAlarm)
        && needsAlarm(otherVesselHistoryData, vesselInfoForAlarm)) {
      vesselsMmsisWithCollisionWarning.add(otherVesselHistoryData.getVessel().getMmsi());
    }
  }

  private boolean validDistanceBetweenVessels(VesselHistoryData otherVesselHistoryData, VesselInfoForAlarm vesselInfoForAlarm) {
    double distanceBetweenVessels = MathUtils.calculateHaversineDistance(
        otherVesselHistoryData.getLatitude(), otherVesselHistoryData.getLongitude(), vesselInfoForAlarm.lat(), vesselInfoForAlarm.lon()
    );
    return distanceBetweenVessels < SAFE_DISTANCE_M;
  }

  private static VesselInfoForAlarm getVesselAlarmInfo(ObjectNode jsonNodeToBeSent) {
    return new VesselInfoForAlarm(
        jsonNodeToBeSent.get("mmsi").asText(),
        jsonNodeToBeSent.get("lat").asDouble(),
        jsonNodeToBeSent.get("lon").asDouble(),
        jsonNodeToBeSent.path("course").asDouble(Double.NaN),
        jsonNodeToBeSent.path("speed").asDouble(Double.NaN)
    );
  }

  private boolean needsAlarm(VesselHistoryData otherVesselHistoryData, VesselInfoForAlarm vesselInfoForAlarm) {
    boolean alarm = false;

    // a) build ENU position vectors (metres)
    Vector2 r0 = enuVector(otherVesselHistoryData.getLatitude(), otherVesselHistoryData.getLongitude(), vesselInfoForAlarm.lat, vesselInfoForAlarm.lon);          // historyData − own

    // b) velocity vectors (m s-¹)
    Vector2 vOwn = velocityVector(vesselInfoForAlarm.course, vesselInfoForAlarm.speed);
    Vector2 vTgt = velocityVector(otherVesselHistoryData.getCourse(), otherVesselHistoryData.getSpeed());
    Vector2 vRel = vTgt.minus(vOwn);

    double vRel2 = vRel.dot(vRel);

    if (vRel2 > 1e-6) {
      double tCpa = -r0.dot(vRel) / vRel2; // seconds

      if (tCpa >= 0 && tCpa <= TIME_HORIZON_S) {
        double dCpa = r0.plus(vRel.times(tCpa)).norm();
        alarm = dCpa < SAFE_DISTANCE_M;
      }
    }
    return alarm;
  }

  // Small-angle ENU projection good for ≤ 100 NM.
  private Vector2 enuVector(double lat, double lon, double refLat, double refLon) {
    double distance = MathUtils.calculateHaversineDistance(lat, lon, refLat, refLon);

    double dLat = Math.toRadians(lat - refLat);
    double dLon = Math.toRadians(lon - refLon);
    double meanLat = Math.toRadians((lat + refLat) * 0.5);

    double east  = MathUtils.R_EARTH * dLon * Math.cos(meanLat);
    double north = MathUtils.R_EARTH * dLat;

    return new Vector2(east, north);
  }

  // Heading (deg CW from N) & speed (knots) → EN velocity (m s-¹)
  private Vector2 velocityVector(double cogDeg, double sogKnots) {
    if (Double.isNaN(cogDeg) || Double.isNaN(sogKnots)) {
      return Vector2.ZERO;
    }
    double sog = sogKnots * 0.514444; // knots → m s-¹
    double hdg = Math.toRadians(cogDeg);
    return new Vector2(sog * Math.sin(hdg), // east
                                 sog * Math.cos(hdg)); // north
  }

  private static boolean verifyDifferentVessels(String ownMmsi, VesselHistoryData otherVesselHistoryData) {
    return !otherVesselHistoryData.getVessel().getMmsi().equals(ownMmsi);
  }

  private static double getVesselHaversineDistanceWithZoneOfInterestCenter(RegisteredUser user, double vesselLatitude, double vesselLongitude) {
    double zoiLatitude = user.getZoneOfInterest().getCenterPointLatitude();
    double zoiLongitude = user.getZoneOfInterest().getCenterPointLongitude();
    return MathUtils.calculateHaversineDistance(vesselLatitude, vesselLongitude, zoiLatitude, zoiLongitude);
  }

  private record VesselInfoForAlarm(String mmsi, double lat, double lon, double course, double speed) {
  }

  // Tiny immutable 2-D vector helper
  public record Vector2(double x, double y) {
    static final Vector2 ZERO = new Vector2(0, 0);

    Vector2 plus(Vector2 v) {
      return new Vector2(x + v.x, y + v.y);
    }

    Vector2 minus(Vector2 v) {
      return new Vector2(x - v.x, y - v.y);
    }

    Vector2 times(double k) {
      return new Vector2(k * x, k * y);
    }

    double dot(Vector2 v) {
      return x * v.x + y * v.y;
    }

    double norm() {
      return Math.hypot(x, y);
    }
  }
}