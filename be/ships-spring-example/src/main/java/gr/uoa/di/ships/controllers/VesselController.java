package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.VesselDTO;
import gr.uoa.di.ships.configurations.exceptions.VesselNotFoundException;
import gr.uoa.di.ships.persistence.model.Vessel;
import gr.uoa.di.ships.persistence.repository.VesselRepository;
import gr.uoa.di.ships.services.interfaces.VesselTypeService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vessel")
class VesselController {

    private final VesselRepository vesselRepository;
    private final VesselTypeService vesselTypeService;

    VesselController(VesselRepository vesselRepository, final VesselTypeService vesselTypeService) {
        this.vesselRepository = vesselRepository;
      this.vesselTypeService = vesselTypeService;
    }


    //
    // e.g., fetch('http://localhost:8080/vessels/99239923').then(res => res.json()).then(console.log)
    // curl -v localhost:8080/vessels
    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/get-all")
    List<Vessel> all() {
        return vesselRepository.findAll();
    }
    // end::get-aggregate-root[]

    @PostMapping("/create")
    void newVessel(@RequestBody VesselDTO vesselDTO) {
      vesselRepository.save(
            Vessel.builder()
                .mmsi(vesselDTO.getMmsi())
                .vesselType(vesselTypeService.findVesselTypeByName(vesselDTO.getType()))
                .build());
    }

    // Single item

    @GetMapping("/{mmsi}")
    Vessel one(@PathVariable String mmsi) {
        return vesselRepository.findByMmsi(mmsi)
            .orElseThrow(() -> new VesselNotFoundException(mmsi));
    }

    @PutMapping("/{mmsi}")
    Vessel replaceVessel(@RequestBody Vessel newVessel, @PathVariable String mmsi) {
      return vesselRepository.findByMmsi(mmsi)
          .map(vessel -> {
            vessel.setMmsi(newVessel.getMmsi());
            return vesselRepository.save(vessel);
          })
          .orElseGet(() -> vesselRepository.save(newVessel));
    }

    @DeleteMapping("/{mmsi}")
    void deleteVessel(@PathVariable String mmsi) {
        vesselRepository.deleteByMmsi(mmsi);
    }
}