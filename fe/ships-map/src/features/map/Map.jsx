import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { Client } from '@stomp/stompjs';
import L, { map } from 'leaflet';
import MouseCoordinates from './MouseCoordinates';
import VesselInfo from '../../components/VesselInfo';
import MapCenterOnOpen from './MapCenterOnOpen';
import {Slider, Button} from '@heroui/react';
import { useMapEvent } from 'react-leaflet';
import { Circle } from 'react-leaflet';


// Ensure Leaflet's default icon assets are set up correctly
import cargoIcon from '../../assets/shipArrows/ship-cargo.png';
import fishingIcon from '../../assets/shipArrows/ship-fishing.png';
import leisureIcon from '../../assets/shipArrows/ship-leisure.png';
import securityIcon from '../../assets/shipArrows/ship-security.png';
import serviceIcon from '../../assets/shipArrows/ship-service.png';
import unknownIcon from '../../assets/shipArrows/ship-unknown.png';

// Define ship type to icon URL mapping
const typeToIconUrl = {
  // Cargo ships
  "tanker-hazarda(major)": cargoIcon,
  "cargo": cargoIcon,
  "cargo-hazarda(major)": cargoIcon,
  "tanker": cargoIcon,
  "cargo-hazardb": cargoIcon,
  "tanker-hazardb": cargoIcon,
  "cargo-hazardd(recognizable)": cargoIcon,
  "tanker-hazardd(recognizable)": cargoIcon,
  "tanker-hazardc(minor)": cargoIcon,
  "cargo-hazardc(minor)": cargoIcon,

  // Fishing vessels
  "fishing": fishingIcon,
  "dredger": fishingIcon,

  // Leisure and pleasure craft
  "sailingvessel": leisureIcon,
  "pleasurecraft": leisureIcon,

  // Security and law enforcement
  "militaryops": securityIcon,
  "sar": securityIcon,
  "pilotvessel": securityIcon,
  "localvessel": securityIcon,
  "divevessel": securityIcon,
  "high-speedcraft": securityIcon,
  "wingingrnd": securityIcon,
  "lawenforce": securityIcon,

  // Service and support vessels
  "anti-pollution": serviceIcon,
  "tug": serviceIcon,
  "specialcraft": serviceIcon,

  // Other types
  "unknown": unknownIcon,
  "other": unknownIcon,
};

// rotateable ship icon factory
const createShipIcon = (heading, type) =>
  L.divIcon({
    className: 'ship-icon',
    html: `<div style="
      transform: rotate(${heading}deg);
      width: 20px;
      height: 20px;
      background: url('${typeToIconUrl[type]}') no-repeat center;
      background-size: contain;
    "></div>`,
    iconSize: [20, 20],
    iconAnchor: [10, 10],
    popupAnchor: [0, -10],
  });

export default function Map({ token, vessels = null, zoneDrawing, onZoneDrawComplete, zone }) {
  const [ships, setShips] = useState({});
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [mapCenter, setMapCenter] = useState([48.30915, -4.91719]);

  const [trackData, setTrackData] = useState([]);
  const [showTrackFor, setShowTrackFor] = useState(null); //mmsi of the ship to show track for
  const [activeTrackIndex, setActiveTrackIndex] = useState(0);
  const [zoneCenter, setZoneCenter] = useState(null);
  const [zoneRadius, setZoneRadius] = useState(null);



  function ZoneClickHandler({
    zoneDrawing,
    zoneCenter,
    onZoneDrawComplete,
    setZoneCenter,
    setZoneRadius
  }) {
    const calculateDistance = (center, edge) => {
      const R = 6371000; // Earth radius in meters
      const lat1 = (center.lat * Math.PI) / 180;
      const lat2 = (edge.lat * Math.PI) / 180;
      const deltaLat = lat2 - lat1;
      const deltaLon = ((edge.lng - center.lng) * Math.PI) / 180;

      const a =
        Math.sin(deltaLat / 2) ** 2 +
        Math.cos(lat1) * Math.cos(lat2) *
        Math.sin(deltaLon / 2) ** 2;
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

      return R * c;
    };

    // Handle click to set center and finalize radius
    useMapEvent('click', (e) => {
      if (!zoneDrawing) return;
      if (!zoneCenter) {
        setZoneCenter(e.latlng);
        console.log("🟢 Center selected at:", e.latlng);
      } else {
        const radius = calculateDistance(zoneCenter, e.latlng);
        setZoneRadius(radius);
        console.log("🔵 Radius finalized:", radius);
        onZoneDrawComplete?.({ center: zoneCenter, radius });
        setZoneCenter(null);
        setZoneRadius(null);
      }
    });

    // Handle mouse move to preview radius
    useMapEvent('mousemove', (e) => {
      if (!zoneDrawing || !zoneCenter) return;
      const radius = calculateDistance(zoneCenter, e.latlng);
      setZoneRadius(radius);
    });

    return null;
  }


  // Detect Tailwind "dark" class on <html>
  useEffect(() => {
    const checkDarkMode = () =>
      document.documentElement.classList.contains('dark');

    setIsDarkMode(checkDarkMode());

    const observer = new MutationObserver(() => {
      setIsDarkMode(checkDarkMode());
    });

    observer.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ['class'],
    });

    return () => observer.disconnect();
  }, []);

  // Endpoint to fetch vessels based on filters
  useEffect(() => {
    if (!vessels) {
      const headers = token
        ? { Authorization: `Bearer ${token}` }
        : {};

      fetch('https://localhost:8443/vessel/get-map', {
        headers
      })
        .then(res => res.json())
        .then(data => {
          const defaultShips = {};
          data.forEach(ship => {
            defaultShips[ship.mmsi] = ship;
          });
          setShips(defaultShips); // Replace with default or filtered vessels
        })
        .catch(err => {
          console.error("Failed to fetch vessels from /vessel/get-map:", err);
        });
    }
  }, [vessels, token]);


  // Replace ships entirely when new filtered vessels come in
  useEffect(() => {
    if (vessels && vessels.length > 0) {
      const initialShips = {};
      vessels.forEach(v => {
        initialShips[v.mmsi] = v;
      });
      setShips(initialShips); // Replace entirely
    } else if (vessels && vessels.length === 0) {
      // If the filters returned nothing, clear the map
      setShips({});
    }
  }, [vessels]);


  // WebSocket connection for real-time vessel updates
  useEffect(() => {
    const brokerURL = `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/socket/websocket`;

    const stompClient = new Client({
      brokerURL,
      connectHeaders: token
        ? { Authorization: `Bearer ${token}` }
        : undefined,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('✅ STOMP CONNECTED!');
        if (token) {
          console.log("Authenticated user detected. Subscribing to personalized messages.");
          stompClient.subscribe(`/user/queue/locations`, message => {
            try {
              const newShip = JSON.parse(message.body);
              // console.log("Received WebSocket Data:", newShip);
              setShips(prev => ({
                ...prev,
                [newShip.mmsi]: newShip // Store ships using MMSI as key
              }));
            } catch (error) {
              console.error("Error parsing personalized WebSocket message:", error);
            }
          });
          stompClient.subscribe(`/user/queue/alerts`, message => {
            try {
              const alert = JSON.parse(message.body);
              // TODO: Show this alert to the user somehow
              console.log("🚨 Alert received:", alert);
            } catch (error) {
              console.error("Error parsing alert WebSocket message:", error);
            }
          });
        } else {
          // Anonymous users: subscribe only to broadcast messages
          console.log("Anonymous user detected. Subscribing to broadcast messages.");
          stompClient.subscribe('/topic/locations', message => {
            try {
              const newShip = JSON.parse(message.body);
              // console.log("Received WebSocket Data:", newShip);
              setShips(prev => ({
                ...prev,
                [newShip.mmsi]: newShip // Store ships using MMSI as key
              }));
            } catch (error) {
              console.error("Error parsing broadcast WebSocket message:", error);
            }
          });
        }
      },
      onStompError: frame => console.error('STOMP ERROR:', frame.headers['message']),
    });

    stompClient.activate();
    return () => stompClient.deactivate();
  }, [token]);

  const tileUrl = isDarkMode
    ? "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
    : "https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png";

  const handleShowTrack = async (mmsi) => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`https://localhost:8443/vessel/get-vessel-history?mmsi=${mmsi}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error("Failed to fetch track");

      const data = await res.json();
      if (data.length === 0) return;

      setTrackData(data);
      setShowTrackFor(mmsi);
      setActiveTrackIndex(data.length - 1); // Start at latest point
      setMapCenter([data[data.length - 1].lat, data[data.length - 1].lon]);
    } catch (err) {
      console.error("Track fetch error:", err);
    }
  };

  
  return (
    <div>
      <MapContainer
        center={mapCenter}
        zoom={6}
        zoomControl={false}
        className='z-0'
        style={{ height: '90vh', width: '100%' }}
        attributionControl={false}
      >
        {/* Custom zoom control */}
        {/* <CustomZoomControl /> */}

        {/* Mouse Coordinates */}
        <MouseCoordinates />
        
        {/* Base: Carto Light */}
        <TileLayer url={tileUrl} />

        {!showTrackFor && Object.values(ships).map(ship => (
          <Marker
            key={ship.mmsi}
            position={[ship.lat, ship.lon]}
            icon={createShipIcon((ship.heading || ship.course || 0), ship.vesselType)}
            eventHandlers={{
              click: () => {
                setMapCenter([ship.lat, ship.lon]);
              }
            }}
          >
            <Popup className="leaflet-custom-popup" closeButton={false}>
              <VesselInfo ship={ship} onShowTrack={handleShowTrack}/>
            </Popup>
          </Marker>
        ))}

        {/* Center on Open */}
        <MapCenterOnOpen position={mapCenter} />

        {trackData.length > 1 && showTrackFor && (
          <>
            <Polyline
              positions={trackData.map(p => [p.lat, p.lon])}
              pathOptions={{ color: 'red', weight: 3 }}
            />
            <Marker
              position={[trackData[activeTrackIndex].lat, trackData[activeTrackIndex].lon]}
              icon={createShipIcon(trackData[activeTrackIndex].heading || 0, trackData[activeTrackIndex].vesselType)}
            />
          </>
        )}

        {/* Zone Drawing */}
        <ZoneClickHandler
          zoneDrawing={zoneDrawing}
          zoneCenter={zoneCenter}
          onZoneDrawComplete={onZoneDrawComplete}
          setZoneCenter={setZoneCenter}
          setZoneRadius={setZoneRadius}
        />

        {/* {zoneCenter && (
          <Circle
            center={zoneCenter}
            radius={zoneRadius}
            pathOptions={{ color: 'blue', dashArray: zoneRadius ? null : '4' }}
          />
        )} */}

        {(zone || zoneCenter) && (
          <Circle
            center={zone ? zone.center : zoneCenter}
            radius={zone ? zone.radius : zoneRadius}
            pathOptions={{ color: 'blue' }}
          />
        )}

      </MapContainer>

      {trackData.length > 1 && showTrackFor && (
        <div className="absolute bottom-10 left-1/2 transform -translate-x-1/2 z-[1000] w-[420px] bg-white/90 dark:bg-black/30 p-2 rounded-2xl flex flex-col items-center gap-1">
          
          <div className="text-xs font-medium text-center">
            {new Date(trackData[activeTrackIndex].timestamp * 1000).toLocaleString()}
          </div>

          <Slider
            size="md"
            color='foreground'
            step={1}
            showSteps={true}
            maxValue={trackData.length - 1}
            defaultValue={trackData.length - 1}
            value={activeTrackIndex}
            onChange={(val) => setActiveTrackIndex(val)}
            className="w-full"
            aria-label="Playback track"
            endContent={
              <Button
                isIconOnly
                size="sm"
                radius='lg'
                onClick={() => setActiveTrackIndex(trackData.length - 1)}
                className="ml-1"
              >
                now
              </Button>
            }
          />

          <Button
            size="sm"
            radius='full'
            variant='ghost'
            color="danger"
            onClick={() => {
              setTrackData([]);
              setShowTrackFor(null);
              setActiveTrackIndex(0);
            }}
          >
            close
          </Button>
        </div>
      )}


    </div>
  );
}
