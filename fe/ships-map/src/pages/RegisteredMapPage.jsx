import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authFetch } from '../utils/authFetch';

import Map from '../features/map/Map';
import SideMenu from '../components/SideMenu';
import TopBar from '../components/TopBar';
import FiltersMenu from '../components/FiltersMenu';
import MyVessels from '../components/MyVessels';
import AlertsMenu from '../components/AlertsMenu';
import NotificationsTab from '../components/Notifications';

export default function RegisteredMapPage({ token, onLogout }) {
  const navigate = useNavigate();
  const [activeMenu, setActiveMenu] = useState(null);
  const [hasActiveFilters, setHasActiveFilters] = useState(false);
  const [hasActiveAlerts, setHasActiveAlerts] = useState(false);
  const [filteredShips, setFilteredShips] = useState(null);
  const [selectedFilters, setSelectedFilters] = useState(null);
  const [previousFilters, setPreviousFilters] = useState(null);
  const [shipList, setShipList] = useState([]);
  const [mapApi, setMapApi] = useState(null);
  const [alerts, setAlerts] = useState({
    speedThreshold: null,
    enterZoneEnabled: false,
    exitZoneEnabled: false,
    collisionsEnabled: false,
  });
  const [zoneDrawing, setZoneDrawing] = useState(false);
  const [zone, setZone] = useState(null);
  const [zoneRefreshToggle, setZoneRefreshToggle] = useState(false);

  useEffect(() => {
    const fetchZone = async () => {
      try {
        const res = await authFetch("https://localhost:8443/zone-of-interest/get-zone", {
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (!res.ok) throw new Error("Failed to fetch zone data.");

        const data = await res.json();

        if (data && data.centerPointLongitude && data.centerPointLatitude && data.radius) {
          setZone({
            id: data.id,
            center: { lat: data.centerPointLatitude, lng: data.centerPointLongitude },
            radius: data.radius,
          });
          console.log("Zone loaded successfully!", data);
        } else {
          // setZone(null);
          console.log("No zone data found or incomplete response.");
        }
      } catch (err) {
        console.error("Error fetching zone data:", err);
      }
    };

    fetchZone();
  }, [zoneRefreshToggle]);


  const handleStartZoneSelection = () => {
    setZoneDrawing(true);
    console.log("Zone drawing started. Click on map to set center.");
  };

  const handleCancelZoneDrawing = () => {
    setZoneDrawing(false);
    console.log("Zone drawing canceled.");
  };

  const handleZoneDrawComplete = async ({ center, radius }) => {
    console.log("Zone completed:", center, radius);
    setZoneDrawing(false);
    setZone({ center, radius });

    try {
      const res = await authFetch("https://localhost:8443/zone-of-interest/set-zone", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          centerPointLatitude: center.lat,
          centerPointLongitude: center.lng,
          radius
        }),
      });

      if (!res.ok) throw new Error("Failed to save zone.");

      setZoneRefreshToggle(prev => !prev);
      console.log(" Zone saved successfully!");
    } catch (err) {
      console.error(" Error saving zone:", err);
    }
  };

  const handleRemoveZone = async () => {
    try {
      if (!zone || !zone.id) {
        console.warn("No zone to delete.");
        return;
      }

      const res = await authFetch(`https://localhost:8443/zone-of-interest/remove-zone?id=${zone.id}`, {
        method: "DELETE",
      });
      if (!res.ok) throw new Error("Failed to delete zone.");
      setZone(null);
      setZoneRefreshToggle(prev => !prev);
      console.log(" Zone deleted successfully!");
    } catch (err) {
      console.error(" Error deleting zone:", err);
    }
  };

  const handleProtectedClick = (label) => {
    console.log(`"${label}" clicked, but guest access. Prompting login.`);
  };

  const toggleMenu = async (menuKey) => {
    setActiveMenu(prev => {
      const next = prev === menuKey ? null : menuKey;

      // Leaving My Fleet → restore previous filters
      if (prev === "My Fleet" && next !== "My Fleet") {
        if (previousFilters) {
          handleFiltersChange(previousFilters);
        } else {
          clearFilters();
        }
      }

      // Entering My Fleet → save filters & load fleet
      if (next === "My Fleet") {
        setPreviousFilters(selectedFilters); // Save current filters

        handleFiltersChange({
          filterFrom: "MyFleet",
          vesselStatusIds: [],
          vesselTypeIds: [],
        });
      }

      return next;
    });
  };

    const handleAlertsChange = async (alertsConfig) => {
    try {
      // Save alerts to backend
      const res = await authFetch("https://localhost:8443/zone-of-interest/set-zone-options", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          maxSpeed: alertsConfig.speedThreshold,
          entersZone: alertsConfig.enterZoneEnabled,
          exitsZone: alertsConfig.exitZoneEnabled,
          collisionMonitoring: alertsConfig.collisionsEnabled,
        }),
      });

      setAlerts(alertsConfig); // Update local state
      setHasActiveAlerts(
        alertsConfig.speedThreshold !== null ||
        alertsConfig.enterZoneEnabled ||
        alertsConfig.exitZoneEnabled ||
        alertsConfig.collisionsEnabled

      );

      if (!res.ok) throw new Error("Failed to save alerts configuration");

      console.log("Alerts saved successfully:", alertsConfig);
    } catch (err) {
      console.error("Error saving alerts configuration:", err);
    }
  };

  const clearAlerts = () => {
    setAlerts({
      speedThreshold: null,
      enterZoneEnabled: false,
      exitZoneEnabled: false,
      collisionsEnabled: false,
    });
    setHasActiveAlerts(false);
    // Optionally notify backend to clear alerts
    handleAlertsChange({
      speedThreshold: null,
      enterZoneEnabled: false,
      exitZoneEnabled: false,
      collisionsEnabled: false,
    });
  };


  const handleFiltersChange = async (filters) => {
    const hasFilters =
      filters.vesselTypeIds?.length > 0 ||
      filters.vesselStatusIds?.length > 0 ||
      (filters.filterFrom && filters.filterFrom !== "All");

    setHasActiveFilters(hasFilters);
    setSelectedFilters(filters);

    try {
      const res = await authFetch("https://localhost:8443/vessel/set-filters-and-get-map", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          filterFrom: filters.filterFrom,
          vesselStatusIds: filters.vesselStatusIds || [],
          vesselTypeIds: filters.vesselTypeIds || [],
        }),
      });

      if (!res.ok) throw new Error("Failed to fetch filtered ships");

      const data = await res.json();
      setFilteredShips(data); // Update map data
    } catch (err) {
      console.error("Error fetching filtered vessels:", err);
    }
  };

  const clearFilters = () => {
    // Clear filters and reset state
    const cleared = {
      filterFrom: "All",
      vesselStatusIds: [],
      vesselTypeIds: [],
    }

    setHasActiveFilters(false);
    setFilteredShips(null);
    setSelectedFilters(null);

    handleFiltersChange(cleared);
  };

  const handleLogout = () => {
    navigate('/');
    onLogout();
  };


  const [fleetRefreshToggle, setFleetRefreshToggle] = useState(false);

  const handleFleetChanged = () => {
    setFleetRefreshToggle(prev => !prev);
  };

  return (
    <div className="relative h-screen w-screen bg-white text-black dark:bg-black dark:text-white overflow-hidden">
      <TopBar
        token={token}
        onLogout={handleLogout}
        ships={shipList}
        onShipSelect={(ship) => mapApi?.focusAndOpenPopup(ship.mmsi)}
      />

      <SideMenu
        userRole={token ? 'user' : 'guest'}
        activeMenu={activeMenu}
        onToggleMenu={toggleMenu}
        onProtectedClick={handleProtectedClick}
      />

      {activeMenu === 'My Fleet' && (
        <MyVessels 
          fleetRefreshToggle={fleetRefreshToggle}
          onLoadFleet={() => {
            handleFiltersChange({
              filterFrom: "MyFleet",
              vesselStatusIds: [],
              vesselTypeIds: [],
            });
          }}
        />
      )}


      {activeMenu === 'Filters' && (
        <FiltersMenu
          selectedFilters={selectedFilters}
          onFiltersChange={handleFiltersChange}
          onClearFilters={clearFilters}
        />
      )}

      {activeMenu === 'Alerts' && (
        <AlertsMenu
          alerts={alerts}
          onAlertsChange={(alertConfig) => {
            handleAlertsChange(alertConfig);
          }}
          onStartZoneSelection={handleStartZoneSelection}
          onRemoveZone={handleRemoveZone}
          zone={zone}
          onCancelZoneDrawing={handleCancelZoneDrawing}
          zoneDrawing={zoneDrawing}
          onClearAlerts={clearAlerts}
        />
      )}

      <NotificationsTab />

      {/* Show filters tag only when filters are enabled */}
      {hasActiveFilters && activeMenu !== "My Fleet" && (
        <div className="fixed top-1/2 translate-y-48 left-4 z-[1200]">
          <div className="flex items-center bg-black text-white text-sm px-3 py-1 rounded-lg shadow-md gap-2">
            <span>Filters</span>
            <button
              onClick={clearFilters}
              className="hover:text-red-400 text-white font-bold"
            >
              ×
            </button>
          </div>
        </div>
      )}

      {/* Show alerts tag only when alerts are enabled */}
      {hasActiveAlerts && activeMenu !== "My Fleet" && (
        <div className="fixed top-1/2 translate-y-56 left-4 z-[1200]">
          <div className="flex items-center bg-black text-white text-sm px-3 py-1 rounded-lg shadow-md gap-2">
            <span>Alerts</span>
            <button
              onClick={clearAlerts}
              className="hover:text-red-400 text-white font-bold"
            >
              ×
            </button>
          </div>
        </div>
      )}

      {/* Main Map Area */}
      <div className="pt-[60px] h-full relative">
        <Map
          token={token}
          vessels={filteredShips}
          zoneDrawing={zoneDrawing}
          onZoneDrawComplete={handleZoneDrawComplete}
          zone={zone}
          onVesselSelect={setMapApi}
          onShipsUpdate={setShipList}
          onFleetChanged={handleFleetChanged}
        />
      </div>
    </div>
  );
}
