package gr.uoa.di.ships.configurations;

import gr.uoa.di.ships.persistence.repository.VesselRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);


    // will run anything that is inside the method before the application starts
    @Bean
    CommandLineRunner initDatabase(VesselRepository repository) {

        return args -> {
          log.info("Used to load Vessels Here");
//            log.info("Preloading " + repository.save(new Vessel("99239923", "cargo")));
//            log.info("Preloading " + repository.save(new Vessel("38883838", "fishing")));
        };
    }
}
