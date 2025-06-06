package gr.uoa.di.ships.services.vessel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import gr.uoa.di.ships.configurations.exceptions.vessel.VesselTypeNotFoundException;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.persistence.repository.vessel.VesselTypeRepository;
import gr.uoa.di.ships.services.implementation.vessel.VesselTypeServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VesselTypeServiceImplTest {

  @Mock
  private VesselTypeRepository vesselTypeRepository;

  @InjectMocks
  private VesselTypeServiceImpl vesselTypeService;

  @Test
  void findVesselTypeByName() {
    // Prepare
    String name = "tempName";

    when(vesselTypeRepository.findVesselTypeByName(name))
        .thenReturn(Optional.of(VesselType.builder().name(name).build()));

    // Execute
    VesselType result = vesselTypeService.findVesselTypeByName(name);

    // Verify
    assertNotNull(result);
    assertEquals(name, result.getName());
  }

  @Test
  void findVesselTypeByName_throwsVesselTypeNotFoundException() {
    // Prepare
    String name = "tempName";

    when(vesselTypeRepository.findVesselTypeByName(name))
        .thenReturn(Optional.empty());

    // Execute and Verify
    assertThrows(
        VesselTypeNotFoundException.class,
        () -> vesselTypeService.findVesselTypeByName(name)
    );
  }
}
