package gr.uoa.di.ships.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import gr.uoa.di.ships.api.dto.MyFleetDTO;
import gr.uoa.di.ships.api.dto.VesselDTO;
import gr.uoa.di.ships.api.mapper.interfaces.VesselMapper;
import gr.uoa.di.ships.configurations.exceptions.vessel.VesselNotFoundException;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.services.implementation.MyFleetServiceImpl;
import gr.uoa.di.ships.services.interfaces.RegisteredUserService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MyFleetServiceImplTest {

  @Mock
  private SeeSeaUserDetailsService seeSeaUserDetailsService;

  @Mock
  private VesselService vesselService;

  @Mock
  private VesselMapper vesselMapper;

  @Mock
  private RegisteredUserService registeredUserService;

  @InjectMocks
  private MyFleetServiceImpl myFleetService;

  @Test
  void addVesselToFleet() {
    // Prepare
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .vessels(new HashSet<>(Set.of(vessel)))
        .build();
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(registeredUserService.getRegisteredUserById(registeredUser.getId()))
        .thenReturn(registeredUser);
    when(vesselService.getVesselByMMSI(vessel.getMmsi())).thenReturn(Optional.of(vessel));

    // Execute
    myFleetService.addVesselToFleet(vessel.getMmsi());
  }

  @Test
  void addVesselToFleet_userVesselsNull_andThrowsVesselNotFoundException() {
    // Prepare
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .build();
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(registeredUserService.getRegisteredUserById(registeredUser.getId()))
        .thenReturn(registeredUser);
    when(vesselService.getVesselByMMSI(vessel.getMmsi())).thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        VesselNotFoundException.class,
        () -> myFleetService.addVesselToFleet(vessel.getMmsi())
    );
  }

  @Test
  void removeVesselFromFleet_nullUserVessels_throwsRuntimeException() {
    // Prepare
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .build();
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(registeredUserService.getRegisteredUserById(registeredUser.getId()))
        .thenReturn(registeredUser);

    // Execute and Verify
    assertThrows(
        RuntimeException.class,
        () -> myFleetService.removeVesselFromFleet(vessel.getMmsi())
    );
  }

  @Test
  void removeVesselFromFleet_vesselNotInFleet_throwsRuntimeException() {
    // Prepare
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .vessels(new HashSet<>())
        .build();
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(registeredUserService.getRegisteredUserById(registeredUser.getId()))
        .thenReturn(registeredUser);

    // Execute and Verify
    assertThrows(
        RuntimeException.class,
        () -> myFleetService.removeVesselFromFleet(vessel.getMmsi())
    );
  }

  @Test
  void removeVesselFromFleet_vesselDoesNotExist_throwsVesselNotFoundException() {
    // Prepare
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .vessels(new HashSet<>(Set.of(vessel)))
        .build();
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(registeredUserService.getRegisteredUserById(registeredUser.getId()))
        .thenReturn(registeredUser);

    when(vesselService.getVesselByMMSI(vessel.getMmsi()))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        VesselNotFoundException.class,
        () -> myFleetService.removeVesselFromFleet(vessel.getMmsi())
    );
  }

  @Test
  void removeVesselFromFleet() {
    // Prepare
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
                                                  .id(1L)
                                                  .vessels(new HashSet<>(Set.of(vessel)))
                                                  .build();
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(registeredUserService.getRegisteredUserById(registeredUser.getId()))
        .thenReturn(registeredUser);

    when(vesselService.getVesselByMMSI(vessel.getMmsi()))
        .thenReturn(Optional.of(vessel));

    // Execute and Verify
    myFleetService.removeVesselFromFleet(vessel.getMmsi());
  }

  @Test
  void getMyFleet() {
    // Prepare
    Vessel vessel = Vessel.builder().mmsi("123").build();
    RegisteredUser registeredUser = RegisteredUser.builder()
        .id(1L)
        .vessels(new HashSet<>(Set.of(vessel)))
        .build();
    when(seeSeaUserDetailsService.getUserDetails())
        .thenReturn(registeredUser);
    when(registeredUserService.getRegisteredUserById(registeredUser.getId()))
        .thenReturn(registeredUser);

    when(vesselMapper.toVesselDTO(vessel))
        .thenReturn(VesselDTO.builder().mmsi(vessel.getMmsi()).build());

    // Execute
    MyFleetDTO result =  myFleetService.getMyFleet();

    // Verify
    assertNotNull(result);
    assertNotNull(result.getMyFleet());
    assertEquals(1, result.getMyFleet().size());
  }
}
