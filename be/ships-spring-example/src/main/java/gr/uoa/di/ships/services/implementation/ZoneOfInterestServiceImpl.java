package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.api.dto.GetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.GetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestDTO;
import gr.uoa.di.ships.api.dto.SetZoneOfInterestOptionsDTO;
import gr.uoa.di.ships.api.mapper.interfaces.ZoneOfInterestMapper;
import gr.uoa.di.ships.configurations.exceptions.ZoneOfInterestNotFoundException;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.ZoneOfInterest;
import gr.uoa.di.ships.persistence.model.ZoneOfInterestOptions;
import gr.uoa.di.ships.persistence.repository.ZoneOfInterestOptionsRepository;
import gr.uoa.di.ships.persistence.repository.ZoneOfInterestRepository;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import gr.uoa.di.ships.services.interfaces.ZoneOfInterestService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class ZoneOfInterestServiceImpl implements ZoneOfInterestService {

  private static final String ZONE_OF_INTEREST_S_DOES_NOT_BELONG_TO_THE_USER_WITH_ID_S = "Zone of Interest with id %s does not belong to the user with id %s";
  private final ZoneOfInterestRepository zoneOfInterestRepository;
  private final SeeSeaUserDetailsService seeSeaUserDetailsService;
  private final RegisteredUserService registeredUserService;
  private final ZoneOfInterestMapper zoneOfInterestMapper;
  private final ZoneOfInterestOptionsRepository zoneOfInterestOptionsRepository;

  public ZoneOfInterestServiceImpl(ZoneOfInterestRepository zoneOfInterestRepository,
                                   SeeSeaUserDetailsService seeSeaUserDetailsService,
                                   RegisteredUserService registeredUserService,
                                   ZoneOfInterestMapper zoneOfInterestMapper,
                                   ZoneOfInterestOptionsRepository zoneOfInterestOptionsRepository) {
    this.zoneOfInterestRepository = zoneOfInterestRepository;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
    this.registeredUserService = registeredUserService;
    this.zoneOfInterestMapper = zoneOfInterestMapper;
    this.zoneOfInterestOptionsRepository = zoneOfInterestOptionsRepository;
  }

  @Override
  public GetZoneOfInterestDTO getZoneOfInterest() {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    return zoneOfInterestMapper.toGetZoneOfInterestDTO(registeredUser.getZoneOfInterest());
  }

  @Override
  public void setZoneOfInterest(SetZoneOfInterestDTO setZoneOfInterestDTO) {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    ZoneOfInterest zoneOfInterest = zoneOfInterestRepository.save(
        ZoneOfInterest.builder()
            .radius(setZoneOfInterestDTO.getRadius())
            .centerPointLatitude(setZoneOfInterestDTO.getCenterPointLatitude())
            .centerPointLongitude(setZoneOfInterestDTO.getCenterPointLongitude())
            .registeredUser(registeredUser)
            .datetimeCreated(LocalDateTime.now(ZoneOffset.UTC))
            .build()
    );
    registeredUser.setZoneOfInterest(zoneOfInterest);
    registeredUserService.updateRegisteredUser(registeredUser);
  }

  @Override
  public void deleteZoneOfInterest(Long id) {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    validateDeletion(id, registeredUser.getId());
    registeredUser.setZoneOfInterest(null);
    registeredUserService.updateRegisteredUser(registeredUser);
    zoneOfInterestRepository.deleteById(id);
    log.info("Zone of Interest with id {} deleted successfully", id);
  }

  @Override
  public void setZoneOfInterestOptions(SetZoneOfInterestOptionsDTO setZoneOfInterestOptionsDTO) {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    ZoneOfInterestOptions zoneOfInterestOptions = registeredUser.getZoneOfInterestOptions();
    if (Objects.isNull(zoneOfInterestOptions)) {
      zoneOfInterestOptions = zoneOfInterestOptionsRepository.save(zoneOfInterestMapper.toZoneOfInterestOptions(setZoneOfInterestOptionsDTO));
      registeredUser.setZoneOfInterestOptions(zoneOfInterestOptions);
    } else {
      zoneOfInterestOptions.setMaxSpeed(setZoneOfInterestOptionsDTO.getMaxSpeed());
      zoneOfInterestOptions.setEntersZone(setZoneOfInterestOptionsDTO.isEntersZone());
      zoneOfInterestOptions.setExitsZone(setZoneOfInterestOptionsDTO.isExitsZone());
      zoneOfInterestOptions.setCollisionMonitoring(setZoneOfInterestOptionsDTO.isCollisionMonitoring());
    }
    resetZoneOfInterestDateTimeCreated(registeredUser);
    registeredUserService.updateRegisteredUser(registeredUser);
  }

  private void resetZoneOfInterestDateTimeCreated(RegisteredUser registeredUser) {
    ZoneOfInterest zoneOfInterest = registeredUser.getZoneOfInterest();
    if (Objects.nonNull(zoneOfInterest)) {
      zoneOfInterest.setDatetimeCreated(LocalDateTime.now(ZoneOffset.UTC));
      zoneOfInterest = zoneOfInterestRepository.save(zoneOfInterest);
      registeredUser.setZoneOfInterest(zoneOfInterest);
    }
  }

  @Override
  public GetZoneOfInterestOptionsDTO getZoneOfInterestOptions() {
    RegisteredUser registeredUser = registeredUserService.getRegisteredUserById(seeSeaUserDetailsService.getUserDetails().getId());
    return zoneOfInterestMapper.toGetZoneOfInterestOptionsDTO(registeredUser.getZoneOfInterestOptions());
  }

  private void validateDeletion(Long zoneOfInterestId, Long userId) {
    ZoneOfInterest zoneOfInterest = zoneOfInterestRepository.findById(zoneOfInterestId)
        .orElseThrow(() -> new ZoneOfInterestNotFoundException(zoneOfInterestId));
    if (!zoneOfInterest.getRegisteredUser().getId().equals(userId)) {
      log.error("User with id {} tried to remove zone of interest with id {} that does not belong to them",
                userId, zoneOfInterestId);
      throw new RuntimeException(ZONE_OF_INTEREST_S_DOES_NOT_BELONG_TO_THE_USER_WITH_ID_S.formatted(zoneOfInterestId, userId));
    }
  }
}