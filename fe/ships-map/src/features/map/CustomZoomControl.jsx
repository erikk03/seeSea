import { useMap } from 'react-leaflet';
import { useEffect } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

export default function CustomZoomControl() {
  const map = useMap();

  useEffect(() => {
    // Add the control
    const zoomControl = L.control.zoom({ position: 'topleft' }); // doesn't matter — we override it
    zoomControl.addTo(map);

    // Reposition and rotate it manually
    const zoomEl = document.querySelector('.leaflet-control-zoom');
    if (zoomEl) {
      zoomEl.style.position = 'fixed'; // place relative to screen
      zoomEl.style.left = '50%';
      zoomEl.style.bottom = '20px';
      zoomEl.style.transform = 'translateX(-50%) rotate(90deg)';
      zoomEl.style.transformOrigin = 'center';
      zoomEl.style.zIndex = 1100;
    }

    return () => {
      map.removeControl(zoomControl);
    };
  }, [map]);

  return null;
}
