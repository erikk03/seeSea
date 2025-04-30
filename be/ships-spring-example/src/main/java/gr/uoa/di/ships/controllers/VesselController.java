package gr.uoa.di.ships.controllers;

import gr.uoa.di.ships.api.dto.VesselDTO;
import gr.uoa.di.ships.persistence.model.Vessel;
import gr.uoa.di.ships.configurations.exceptions.VesselNotFoundException;
import gr.uoa.di.ships.persistence.repository.VesselRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vessel")
//@PreAuthorize("hasRole('ADMIN')")
class VesselController {

    private final VesselRepository vesselRepository;

    VesselController(VesselRepository vesselRepository) {
        this.vesselRepository = vesselRepository;
    }


    //
    // e.g., fetch('http://localhost:8080/vessels/99239923').then(res => res.json()).then(console.log)
    // curl -v localhost:8080/vessels
    // Aggregate root
    // tag::get-aggregate-root[]
    @CrossOrigin(origins ="${cors.urls}")
    @GetMapping("/get-all")
    List<Vessel> all() {
        return vesselRepository.findAll();
    }
    // end::get-aggregate-root[]

    @CrossOrigin(origins ="${cors.urls}")
    @PostMapping("/create")
    Vessel newVessel(@RequestBody VesselDTO vesselDTO) {
        return vesselRepository.save(
            Vessel.builder()
                  .mmsi(vesselDTO.getMmsi())
                  .build());
    }

    // Single item

    @CrossOrigin(origins ="${cors.urls}")
    @GetMapping("/{mmsi}")
    Vessel one(@PathVariable String mmsi) {

        return vesselRepository.findByMmsi(mmsi)
                               .orElseThrow(() -> new VesselNotFoundException(mmsi));
    }

    @CrossOrigin(origins ="${cors.urls}")
    @PutMapping("/{mmsi}")
    Vessel replaceVessel(@RequestBody Vessel newVessel, @PathVariable String mmsi) {
      return vesselRepository.findByMmsi(mmsi)
          .map(vessel -> {
            vessel.setMmsi(newVessel.getMmsi());
            return vesselRepository.save(vessel);
          })
          .orElseGet(() -> vesselRepository.save(newVessel));
    }

    @CrossOrigin(origins ="${cors.urls}")
    @DeleteMapping("/{mmsi}")
    void deleteVessel(@PathVariable String mmsi) {
        vesselRepository.deleteByMmsi(mmsi);
    }
}