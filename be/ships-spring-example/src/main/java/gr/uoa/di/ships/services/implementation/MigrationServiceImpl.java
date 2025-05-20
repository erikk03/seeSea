package gr.uoa.di.ships.services.implementation;

import com.opencsv.CSVReader;
import gr.uoa.di.ships.configurations.exceptions.MigrationNotFoundException;
import gr.uoa.di.ships.persistence.model.Migration;
import gr.uoa.di.ships.persistence.model.enums.MigrationEnum;
import gr.uoa.di.ships.persistence.model.vessel.Vessel;
import gr.uoa.di.ships.persistence.model.vessel.VesselStatus;
import gr.uoa.di.ships.persistence.model.vessel.VesselType;
import gr.uoa.di.ships.persistence.repository.MigrationRepository;
import gr.uoa.di.ships.services.interfaces.MigrationService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselStatusService;
import gr.uoa.di.ships.services.interfaces.vessel.VesselTypeService;
import jakarta.persistence.EntityManager;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class MigrationServiceImpl implements MigrationService {

  private static final String ASSETS_VESSEL_TYPES_CSV = "assets/vessel_types.csv";
  private static final String ASSETS_VESSEL_STATUS_CSV = "assets/vessel_status.csv";
  private final VesselTypeService vesselTypeService;
  private final VesselStatusService vesselStatusService;
  private final VesselService vesselService;
  private final MigrationRepository migrationRepository;
  private final EntityManager entityManager;

  public MigrationServiceImpl(VesselTypeService vesselTypeService,
                              VesselStatusService vesselStatusService,
                              VesselService vesselService,
                              MigrationRepository migrationRepository,
                              EntityManager entityManager) {
    this.vesselTypeService = vesselTypeService;
    this.vesselStatusService = vesselStatusService;
    this.vesselService = vesselService;
    this.migrationRepository = migrationRepository;
    this.entityManager = entityManager;
  }

  @Override
  public void loadVesselTypesFromCSV() {
    Migration migration = validateMigration(MigrationEnum.LOAD_VESSEL_TYPES_CSV);
    try (
        InputStream inputStream = new ClassPathResource(ASSETS_VESSEL_TYPES_CSV).getInputStream();
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))
        ) {
      Map<String, VesselType> vesselTypeMap = vesselTypeService.findAllVesselTypes()
          .stream()
          .collect(Collectors.toMap(VesselType::getName, Function.identity()));
      Set<String> existingMmsis = vesselService.getAllVessels()
          .stream()
          .map(Vessel::getMmsi)
          .collect(Collectors.toSet());
      List<Vessel> newVessels = new ArrayList<>();
      readVesselTypesCsv(csvReader, vesselTypeMap, existingMmsis, newVessels);
      vesselService.saveAllVessels(newVessels);
      updateMigration(migration);
      entityManager.flush();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load vessel types from CSV", e);
    }
  }

  @Override
  public void loadVesselStatusFromCSV() {
    Migration migration = validateMigration(MigrationEnum.LOAD_VESSEL_STATUS_CSV);
    try (
        InputStream inputStream = new ClassPathResource(ASSETS_VESSEL_STATUS_CSV).getInputStream();
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))
    ) {
      Map<String, VesselStatus> vesselStatusMap = vesselStatusService.findAllVesselStatuses()
          .stream()
          .collect(Collectors.toMap(VesselStatus::getName, Function.identity()));
      readVesselStatusesCsv(csvReader, vesselStatusMap);
      updateMigration(migration);
      entityManager.flush();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load vessel statuses from CSV", e);
    }
  }

  @Override
  public boolean completedMigration(MigrationEnum migrationEnum) {
    Migration migration = migrationRepository.findByDescription(migrationEnum.getDescription())
        .orElseThrow(() -> new MigrationNotFoundException(migrationEnum.getDescription()));
    return migration.getDone();
  }

  private void updateMigration(Migration migration) {
    migration.setDone(true);
    migrationRepository.save(migration);
  }

  private Migration validateMigration(MigrationEnum migrationEnum) {
    Migration migration = migrationRepository.findByDescription(migrationEnum.getDescription())
        .orElseThrow(() -> new MigrationNotFoundException(migrationEnum.getDescription()));
    if (migration.getDone()) {
      throw new RuntimeException("Migration: \"" + migrationEnum.getDescription() + "\" already completed");
    }
    return migration;
  }

  private void readVesselStatusesCsv(CSVReader csvReader, Map<String, VesselStatus> vesselStatusMap) {
    StreamSupport.stream(csvReader.spliterator(), false).forEach(row -> {
      if (row.length < 2) {
        return;
      }
      Long statusId = Long.parseLong(row[0].trim());
      String statusName = row[1].trim();
      persistVesselStatus(vesselStatusMap, statusId, statusName);
    });
  }

  private void persistVesselStatus(Map<String, VesselStatus> vesselStatusMap, Long statusId, String statusName) {
    vesselStatusMap.computeIfAbsent(statusName, name -> {
      VesselStatus vesselStatus = new VesselStatus();
      vesselStatus.setId(statusId);
      vesselStatus.setName(name);
      return vesselStatusService.saveVesselStatus(vesselStatus);
    });
  }

  private void readVesselTypesCsv(CSVReader csvReader, Map<String, VesselType> vesselTypeMap, Set<String> existingMmsis, List<Vessel> newVessels) {
    StreamSupport.stream(csvReader.spliterator(), false).forEach(row -> {
      if (row.length < 2) {
        return;
      }
      String mmsi = row[0].trim();
      String typeName = row[1].trim();
      VesselType vesselType = persistVesselType(vesselTypeMap, typeName);
      createVessel(existingMmsis, mmsi, vesselType, newVessels);
    });
  }

  private VesselType persistVesselType(Map<String, VesselType> vesselTypeMap, String typeName) {
    return vesselTypeMap.computeIfAbsent(typeName, name -> {
      VesselType vesselType = new VesselType();
      vesselType.setName(name);
      return vesselTypeService.saveVesselType(vesselType);
    });
  }

  private void createVessel(Set<String> existingMmsis, String mmsi, VesselType vesselType, List<Vessel> vessels) {
    if (!existingMmsis.contains(mmsi)) {
      Vessel vessel = new Vessel();
      vessel.setMmsi(mmsi);
      vessel.setVesselType(vesselType);
      vessels.add(vessel);
      existingMmsis.add(mmsi);
    }
  }
}