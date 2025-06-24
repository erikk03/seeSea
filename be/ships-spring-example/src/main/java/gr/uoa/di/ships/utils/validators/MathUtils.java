package gr.uoa.di.ships.utils.validators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MathUtils {

  public static final double R_EARTH = 6371000; // Earth radius in meters

  public static double calculateHaversineDistance(double latitude2, double longitude2, double latitude1, double longitude1) {
    double lat2 = Math.toRadians(latitude2);
    double lon2 = Math.toRadians(longitude2);
    double lat1 = Math.toRadians(latitude1);
    double lon1 = Math.toRadians(longitude1);

    double deltaLat = lat2 - lat1;
    double deltaLon = lon2 - lon1;

    double a = Math.pow(Math.sin(deltaLat / 2), 2)
        + Math.cos(lat1) * Math.cos(lat2)
        * Math.pow(Math.sin(deltaLon / 2), 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R_EARTH * c; // distance in meters
  }


}
