package gr.uoa.di.ships.services;

import gr.uoa.di.ships.services.interfaces.vessel.VesselHistoryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class SchedulerService {

  private final VesselHistoryDataService vesselHistoryDataService;

  public SchedulerService(VesselHistoryDataService vesselHistoryDataService) {
    this.vesselHistoryDataService = vesselHistoryDataService;
  }

  @Scheduled(cron = "${spring.scheduler.cron}", zone = "${spring.scheduler.timezone}")
  public void scheduleVesselHistoryDataCleanup() {
    log.info("Vessel History Data Cleanup Scheduler Started");
    vesselHistoryDataService.deleteOldVesselHistoryData();
    log.info("Vessel History Data Cleanup Scheduler Finished");
  }
}