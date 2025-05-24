import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import WebSocketMap from '../features/map/WebSocketMap';
import SideMenu from '../components/SideMenu';
import TopBar from '../components/TopBar';
import { Button } from '@heroui/react';
import FiltersMenu from '../components/FiltersMenu';

export default function RegisteredMapPage({ token, onLogout }) {
  const navigate = useNavigate();
  const [activeMenu, setActiveMenu] = useState(null);

  const handleProtectedClick = (label) => {
    console.log(`"${label}" clicked, but guest access. Prompting login.`);
  };

  const toggleMenu = (menuKey) => {
    setActiveMenu(prev => (prev === menuKey ? null : menuKey));
  }

  // Redirect to signin page and signup page
  const handleSignIn = () => navigate('/signin');
  const handleSignUp = () => navigate('/signup');
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
      />

      <SideMenu
        userRole={token ? 'user' : 'guest'}
        activeMenu={activeMenu}
        onToggleMenu={toggleMenu}
        onProtectedClick={handleProtectedClick}
      />

      <div className="pt-[60px] h-full relative">
        <WebSocketMap token={token} />
      </div>

      {activeMenu === 'Filters' && <FiltersMenu />}

    </div>
  );
}
