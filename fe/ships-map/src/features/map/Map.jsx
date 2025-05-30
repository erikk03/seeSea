import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { Client } from '@stomp/stompjs';
import L, { map } from 'leaflet';
import MouseCoordinates from './MouseCoordinates';
import VesselInfo from '../../components/VesselInfo';
import MapCenterOnOpen from './MapCenterOnOpen';

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

export default function Map({ token, vessels = null }) {
  const [ships, setShips] = useState({});
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [mapCenter, setMapCenter] = useState([48.30915, -4.91719]);


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

        {Object.values(ships).map(ship => (
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
              <VesselInfo ship={ship} />
            </Popup>
          </Marker>
        ))}

        {/* Center on Open */}
        <MapCenterOnOpen position={mapCenter} />


      </MapContainer>
    </div>
  );
}
