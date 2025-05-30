import { useMap } from 'react-leaflet';
import { useEffect } from 'react';

export default function MapCenterOnOpen({ position }) {
  const map = useMap();

  useEffect(() => {
    if (position) {
      map.setView(position, map.getZoom(), {
        animate: true,
      });
    }
  }, [position, map]);

  return null;
}
