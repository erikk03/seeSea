import React, { useState, useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { Client } from '@stomp/stompjs';
import MouseCoordinates from './MouseCoordinates';
import VesselInfo from '../../components/VesselInfo';
import MapCenterOnOpen from './MapCenterOnOpen';
import {Slider, Button} from '@heroui/react';
import { createShipIcon } from '../../utils/shipIcons';

export default function Map({ token, vessels = null, onVesselSelect, onShipsUpdate }) {
  const [ships, setShips] = useState({});
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [mapCenter, setMapCenter] = useState([48.30915, -4.91719]);

  const [trackData, setTrackData] = useState([]);
  const [showTrackFor, setShowTrackFor] = useState(null); //mmsi of the ship to show track for
  const [activeTrackIndex, setActiveTrackIndex] = useState(0);
  const markerRefs = useRef({});


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
          onShipsUpdate?.(Object.values(defaultShips)); // Notify parent of new ships
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

  useEffect(() => {
    if (onVesselSelect) {
      onVesselSelect({
        focusAndOpenPopup: (mmsi) => {
          const ship = ships[mmsi];
          if (!ship) return;
          setMapCenter([ship.lat, ship.lon]);

          setTimeout(() => {
            const marker = markerRefs.current[mmsi];
            if (marker) marker.openPopup();
          }, 100); // delay ensures map has moved before popup opens
        }
      });
    }
  }, [ships, onVesselSelect]);

  
  return (
    <div>    
      <MapContainer
        center={mapCenter}
        zoom={6}
        zoomControl={false}
        className='z-0'
        style={{ height: '90vh', width: '100%' }}
        attributionControl={false}
        maxBounds={[
          [-85, -180], // Southwest corner
          [85, 180]    // Northeast corner
        ]}
        maxBoundsViscosity={1.0}
        worldCopyJump={false}
        noWrap={true}
        minZoom={3}
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
            ref={(ref) => { if (ref) markerRefs.current[ship.mmsi] = ref; }}
            eventHandlers={{
              click: () => {
                setMapCenter([ship.lat, ship.lon]); // ← Focus
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

        {trackData.length >= 1 && showTrackFor && (
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

      </MapContainer>

      {trackData.length > 1 && showTrackFor && (
        <div className="absolute bottom-10 left-1/2 transform -translate-x-1/2 z-[1000] w-[420px] bg-white/70 dark:bg-black/30 p-2 rounded-2xl flex flex-col items-center gap-1">
          
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

      {trackData.length <= 1 && showTrackFor && (
        <div className="absolute bottom-10 left-1/2 transform -translate-x-1/2 z-[1000] w-[420px] bg-white/70 dark:bg-black/30 p-2 rounded-2xl flex flex-col items-center gap-1">
          <div className="text-xs font-medium text-center">
            No track data available for MMSI: {showTrackFor}
          </div>

          <Button
            size="sm"
            radius='full'
            variant='ghost'
            color='default'
            onClick={() => {
              setTrackData([]);
              setShowTrackFor(null);
              setActiveTrackIndex(0);
            }}
          >
            RETURN
          </Button>
        </div>
      )}

    </div>
  );
}
