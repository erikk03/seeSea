import { useState } from 'react';
import { useMapEvents } from 'react-leaflet';

export default function MouseCoordinates() {
  const [coords, setCoords] = useState(null);

  useMapEvents({
    mousemove(e) {
      setCoords(e.latlng);
    },
    mouseout() {
      setCoords(null);
    },
  });

  return coords ? (
    <div className="absolute bottom-2 right-2 z-[1000] bg-white/80 text-xs text-black px-3 py-1 rounded shadow">
      Lat: {coords.lat.toFixed(5)}, Lng: {coords.lng.toFixed(5)}
    </div>
  ) : null;
}
