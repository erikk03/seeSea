package gr.uoa.di.ships.services;

import gr.uoa.di.ships.vessels.Vessel;
import gr.uoa.di.ships.vessels.VesselNotFoundException;
import gr.uoa.di.ships.vessels.VesselRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
    @GetMapping("/vessels")
    List<Vessel> all() {
        return repository.findAll();
    }
    // end::get-aggregate-root[]

    @CrossOrigin(origins ="${cors.urls}")
    @PostMapping("/vessels")
    Vessel newVessel(@RequestBody Vessel newVessel) {
        return repository.save(newVessel);
    }

    // Single item

    @CrossOrigin(origins ="${cors.urls}")
    @GetMapping("/vessels/{mmsi}")
    Vessel one(@PathVariable String mmsi) {

        return repository.findById(mmsi)
                .orElseThrow(() -> new VesselNotFoundException(mmsi));
    }

    @CrossOrigin(origins ="${cors.urls}")
    @PutMapping("/vessels/{mmsi}")
    Vessel replaceVessel(@RequestBody Vessel newVessel, @PathVariable String mmsi) {

        return repository.findById(mmsi)
                .map(vessel -> {
                    vessel.setMmsi(newVessel.getMmsi());
                    vessel.setType(newVessel.getMmsi());
                    return repository.save(vessel);
                })
                .orElseGet(() -> {
                    return repository.save(newVessel);
                });
    }

    @CrossOrigin(origins ="${cors.urls}")
    @DeleteMapping("/vessels/{mmsi}")
    void deleteVessel(@PathVariable String mmsi) {
        repository.deleteById(mmsi);
    }
}