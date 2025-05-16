package gr.uoa.di.ships.services.implementation;

import gr.uoa.di.ships.services.interfaces.SchedulerService;
import gr.uoa.di.ships.services.interfaces.VesselHistoryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

  private final VesselHistoryDataService vesselHistoryDataService;

  public SchedulerServiceImpl(VesselHistoryDataService vesselHistoryDataService) {
    this.vesselHistoryDataService = vesselHistoryDataService;
  }

  @Scheduled(cron = "${spring.scheduler.cron}", zone = "${spring.scheduler.timezone}")
  public void scheduleVesselHistoryDataCleanup() {
    log.info("Vessel History Data Cleanup Scheduler Started");
    vesselHistoryDataService.deleteOldVesselHistoryData();
    log.info("Vessel History Data Cleanup Scheduler Finished");
  }
}