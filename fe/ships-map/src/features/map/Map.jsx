import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { Client } from '@stomp/stompjs';
import L from 'leaflet';
import MouseCoordinates from './MouseCoordinates';


// rotateable ship icon factory
const createShipIcon = heading =>
  L.divIcon({
    className: 'ship-icon',
    html: `<div style="
      transform: rotate(${heading}deg);
      width: 20px;
      height: 20px;
      background: url('/ship-arrow.png') no-repeat center;
      background-size: contain;
    "></div>`,
    iconSize: [20, 20],
    iconAnchor: [10, 10],
    popupAnchor: [0, -10],
  });

export default function Map({ token, vessels = null }) {
  const [ships, setShips] = useState({});
  const [isDarkMode, setIsDarkMode] = useState(false);

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


  // WebSocket connection for real-time updates
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
        } else {
          // Anonymous users: subscribe only to broadcast messages
          console.log("Anonymous user detected. Subscribing to broadcast messages.");
          stompClient.subscribe('/topic/locations', message => {
            try {
              const newShip = JSON.parse(message.body);
              console.log("Received WebSocket Data:", newShip);
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
        center={[48.30915, -4.91719]}
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
            icon={createShipIcon(ship.heading || ship.course || 0)}
          >
            <Popup>
              <div>
                <div><strong>MMSI:</strong> {ship.mmsi}</div>
                <div><strong>Status:</strong> {ship.status}</div>
                <div><strong>Speed:</strong> {ship.speed} kn</div>
                <div><strong>Course:</strong> {ship.course}°</div>
                <div><strong>Heading:</strong> {ship.heading}°</div>
                <div>
                  <strong>Time:</strong>{' '}
                  {new Date(ship.timestamp * 1000).toLocaleString()}
                </div>
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}
