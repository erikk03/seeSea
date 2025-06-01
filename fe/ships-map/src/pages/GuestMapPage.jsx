import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Map from '../features/map/Map';
import SideMenu from '../components/SideMenu';
import TopBar from '../components/TopBar';
import { Button } from '@heroui/react';
import LoginModal from '../features/auth/LoginModal';

export default function GuestMapPage({ token, onLogout, onLogin }) {
  const navigate = useNavigate();
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);
    const [shipList, setShipList] = useState([]);
    const [mapApi, setMapApi] = useState(null);

  const handleProtectedClick = (label) => {
    console.log(`"${label}" clicked, but guest access. Prompting login.`);
    setShowLoginPrompt(true);
  };

  // Redirect to signin and signup pages
  const handleSignIn = () => navigate('/signin');
  const handleSignUp = () => navigate('/register');

  // Clear token + optionally redirect
  const handleLogout = () => {
    navigate('/');
    onLogout();
  }

  return (
    <div className="relative h-screen w-screen bg-white text-black dark:bg-black dark:text-white overflow-hidden">
      <TopBar
        token={token}
        onSignIn={handleSignIn}
        onSignUp={handleSignUp}
        onLogout={handleLogout}
        ships={shipList}
        onShipSelect={(ship) => mapApi?.focusAndOpenPopup(ship.mmsi)}
      />

      <SideMenu userRole={token ? 'user' : 'guest'} onProtectedClick={handleProtectedClick} />

      <div className="pt-[60px] h-full">
        <Map
        token={token}
        onVesselSelect={setMapApi}
        onShipsUpdate={setShipList}
        />
      </div>

      <LoginModal
        isOpen={showLoginPrompt}
        onClose={() => setShowLoginPrompt(false)}
        onLogin={(token) => {
        onLogin(token);
        setShowLoginPrompt(false);
      }}

      />
    </div>
  );
}
