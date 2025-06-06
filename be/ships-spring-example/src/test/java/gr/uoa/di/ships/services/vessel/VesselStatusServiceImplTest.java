package gr.uoa.di.ships.services.vessel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import gr.uoa.di.ships.configurations.exceptions.vessel.VesselStatusNotFoundException;
import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import gr.uoa.di.ships.persistence.repository.vessel.VesselStatusRepository;
import gr.uoa.di.ships.services.implementation.vessel.VesselStatusServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VesselStatusServiceImplTest {

  @Mock
  private VesselStatusRepository vesselStatusRepository;

  @InjectMocks
  private VesselStatusServiceImpl vesselStatusService;

  @Test
  void getVesselStatusById() {
    // Prepare
    Long statusId = 1L;

    when(vesselStatusRepository.findById(statusId))
        .thenReturn(Optional.of(VesselStatus.builder().name("tempVesselStatusName").build()));

    when(vesselStatusRepository.findById(15L))
        .thenReturn(Optional.of(new VesselStatus()));

    // Execute
    VesselStatus result = vesselStatusService.getVesselStatusById(statusId);

    // Verify
    assertNotNull(result);
    assertEquals("tempVesselStatusName", result.getName());
  }

  @Test
  void getVesselStatusById_throwsVesselStatusNotFoundException() {
    // Prepare
    Long status = 1L;

    when(vesselStatusRepository.findById(status))
        .thenReturn(Optional.empty());

    when(vesselStatusRepository.findById(15L))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        VesselStatusNotFoundException.class,
        () -> vesselStatusService.getVesselStatusById(status)
    );
  }
}
