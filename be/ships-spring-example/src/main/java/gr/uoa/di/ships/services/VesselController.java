package gr.uoa.di.ships.services;

import gr.uoa.di.ships.vessels.Vessel;
import gr.uoa.di.ships.vessels.VesselNotFoundException;
import gr.uoa.di.ships.vessels.VesselRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vessel")
@PreAuthorize("hasRole('ADMIN')")
class VesselController {

    private final VesselRepository repository;

    VesselController(VesselRepository repository) {
        this.repository = repository;
    }


    //
    // e.g., fetch('http://localhost:8080/vessels/99239923').then(res => res.json()).then(console.log)
    // curl -v localhost:8080/vessels
    // Aggregate root
    // tag::get-aggregate-root[]
    @CrossOrigin(origins ="${cors.urls}")
    @GetMapping("/get-all")
    List<Vessel> all() {
        return repository.findAll();
    }
    // end::get-aggregate-root[]

    @CrossOrigin(origins ="${cors.urls}")
    @PostMapping("/create")
    Vessel newVessel(@RequestBody VesselDTO vesselDTO) {
        return repository.save(
            Vessel.builder()
                  .mmsi(vesselDTO.mmsi)
                  .build());
    }

    // Single item

    @CrossOrigin(origins ="${cors.urls}")
    @GetMapping("/{mmsi}")
    Vessel one(@PathVariable String mmsi) {

        return repository.findById(mmsi)
                .orElseThrow(() -> new VesselNotFoundException(mmsi));
    }

    @CrossOrigin(origins ="${cors.urls}")
    @PutMapping("/{mmsi}")
    Vessel replaceVessel(@RequestBody Vessel newVessel, @PathVariable String mmsi) {

        return repository.findById(mmsi)
                .map(vessel -> {
                    vessel.setMmsi(newVessel.getMmsi());
                    return repository.save(vessel);
                })
                .orElseGet(() -> {
                    return repository.save(newVessel);
                });
    }

    @CrossOrigin(origins ="${cors.urls}")
    @DeleteMapping("/{mmsi}")
    void deleteVessel(@PathVariable String mmsi) {
        repository.deleteById(mmsi);
    }
}