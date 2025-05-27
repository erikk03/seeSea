package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.MyFleetDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselMapper;
import gr.uoa.di.ships.configurations.exceptions.vessel.VesselNotFoundException;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.services.interfaces.MyFleetService;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class MyFleetServiceImpl implements MyFleetService {

  private static final String USER_HAS_NO_VESSELS_IN_FLEET = "User has no vessels in fleet";
  public static final String VESSEL_WITH_MMSI_S_IS_NOT_IN_THE_USER_S_FLEET = "Vessel with mmsi %s is not in the user's fleet";

  private final SeeSeaUserDetailsService seeSeaUserDetailsService;
  private final VesselService vesselService;
  private final VesselMapper vesselMapper;
  private final RegisteredUserService registeredUserService;

  public MyFleetServiceImpl(SeeSeaUserDetailsService seeSeaUserDetailsService,
                            VesselService vesselService,
                            VesselMapper vesselMapper,
                            RegisteredUserService registeredUserService) {
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
    this.vesselService = vesselService;
    this.vesselMapper = vesselMapper;
    this.registeredUserService = registeredUserService;
  }

  @Override
  public void addVesselToFleet(String mmsi) {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    Set<Vessel> registeredUserVessels = Objects.nonNull(registeredUser.getVessels())
        ? registeredUser.getVessels()
        : new HashSet<>();
    registeredUserVessels.add(vesselService.getVesselByMMSI(mmsi).orElseThrow(() -> new VesselNotFoundException(mmsi)));
    registeredUser.setVessels(registeredUserVessels);
    registeredUserService.updateRegisteredUser(registeredUser);
  }

  @Override
  public void removeVesselFromFleet(String mmsi) {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    Set<Vessel> registeredUserVessels = registeredUser.getVessels();
    if (Objects.isNull(registeredUserVessels)) {
      throw new RuntimeException(USER_HAS_NO_VESSELS_IN_FLEET);
    }
    if (registeredUser.getVessels().stream().noneMatch(vessel -> vessel.getMmsi().equals(mmsi))) {
      throw new RuntimeException(VESSEL_WITH_MMSI_S_IS_NOT_IN_THE_USER_S_FLEET.formatted(mmsi));
    }
    registeredUserVessels.remove(vesselService.getVesselByMMSI(mmsi).orElseThrow(() -> new VesselNotFoundException(mmsi)));
  }

  @Override
  public MyFleetDTO getMyFleet() {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    return MyFleetDTO.builder()
        .myFleet(registeredUser.getVessels().stream().map(vesselMapper::toVesselDTO).toList())
        .build();
  }
}