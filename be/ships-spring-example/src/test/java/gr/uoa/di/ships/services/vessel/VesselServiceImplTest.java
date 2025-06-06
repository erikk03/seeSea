package gr.uoa.di.ships.services.vessel;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import gr.uoa.di.ships.api.dto.UpdateVesselDTO;
import gr.uoa.di.ships.configurations.exceptions.vessel.VesselNotFoundException;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.persistence.repository.vessel.VesselRepository;
import gr.uoa.di.ships.services.implementation.vessel.VesselServiceImpl;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VesselServiceImplTest {

  @Mock
  private VesselRepository vesselRepository;

  @Mock
  private VesselTypeService vesselTypeService;

  @InjectMocks
  private VesselServiceImpl vesselService;

  @Test
  void updateVesselType() {
    // Prepare
    UpdateVesselDTO updateVesselDTO = UpdateVesselDTO.builder()
        .mmsi("123")
        .newType("tempNewType")
        .build();

    when(vesselRepository.findByMmsi(updateVesselDTO.getMmsi()))
        .thenReturn(Optional.of(new Vessel()));

    when(vesselTypeService.findVesselTypeByName(updateVesselDTO.getNewType()))
        .thenReturn(VesselType.builder().name("tempNewType").build());

    // Execute
    vesselService.updateVesselType(updateVesselDTO);
  }

  @Test
  void updateVesselType_throwVesselNotFoundException() {
    // Prepare
    UpdateVesselDTO updateVesselDTO = UpdateVesselDTO.builder()
        .mmsi("123")
        .newType("tempNewType")
        .build();

    when(vesselRepository.findByMmsi(updateVesselDTO.getMmsi()))
        .thenReturn(Optional.empty());

    // Execute
    assertThrows(
        VesselNotFoundException.class,
        () -> vesselService.updateVesselType(updateVesselDTO)
    );
  }
}
