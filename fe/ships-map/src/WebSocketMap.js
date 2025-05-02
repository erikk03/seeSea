import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import L from 'leaflet';

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

export default function WebSocketMap({ token, onLogout }) {
  const [ships, setShips] = useState({});

  useEffect(() => {
    const socket = new SockJS('https://localhost:8443/ws');
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        console.log("Connected to WebSocket (SockJS)");

        stompClient.subscribe('/topic/locations', message => {  // from lecture: maybe a path with userId?
          try {
            const newShip = JSON.parse(message.body);
            console.log("Received WebSocket Data:", newShip);

            setShips((prevShips) => ({
              ...prevShips,
              [newShip.mmsi]: newShip, // Store ships using MMSI as key
            }));
          } catch (error) {
            console.error("Error parsing WebSocket message:", error);
          }
        });
      },
      onStompError: frame => {
        console.error('STOMP Error:', frame.headers['message']);
      },
    });

    stompClient.activate();
    return () => stompClient.deactivate();
  }, [token]);

  return (
    <div>
      <header style={{ display: 'flex', justifyContent: 'space-between', padding: '1rem' }}>
        <h1>Live Ship Map</h1>
        <button onClick={onLogout}>Log Out</button>
      </header>

      <MapContainer
        center={[37.9838, 23.7275]}
        zoom={6}
        style={{ height: '80vh', width: '100%' }}
      >
        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
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
