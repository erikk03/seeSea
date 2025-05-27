package gr.uoa.di.ships.services.interfaces;

import gr.uoa.di.ships.api.dto.MyFleetDTO;

public interface MyFleetService {

  void addVesselToFleet(String mmsi);

  void removeVesselFromFleet(String mmsi);

  MyFleetDTO getMyFleet();
}
