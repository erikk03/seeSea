import L, { map } from 'leaflet';

import cargoIcon from '../assets/shipArrows/ship-cargo.png';
import fishingIcon from '../assets/shipArrows/ship-fishing.png';
import leisureIcon from '../assets/shipArrows/ship-leisure.png';
import securityIcon from '../assets/shipArrows/ship-security.png';
import serviceIcon from '../assets/shipArrows/ship-service.png';
import unknownIcon from '../assets/shipArrows/ship-unknown.png';

// Define ship type to icon URL mapping
export const typeToIconUrl = {
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
  "passenger": leisureIcon,

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
export const createShipIcon = (heading, type) =>
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