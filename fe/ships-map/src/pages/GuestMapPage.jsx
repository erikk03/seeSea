import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import WebSocketMap from '../features/map/WebSocketMap';
import SideMenu from '../components/SideMenu';
import TopBar from '../components/TopBar';
import { Button } from '@heroui/react';

export default function GuestMapPage() {
  const navigate = useNavigate();
  const [token, setToken] = useState(() => localStorage.getItem('token') || '');
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);

  const handleProtectedClick = (label) => {
    console.log(`"${label}" clicked, but guest access. Prompting login.`);
    setShowLoginPrompt(true);
  };

  // Redirect to signin page
  const handleSignIn = () => {
    navigate('/signin');
  };

  // Redirect to signup page
  const handleSignUp = () => {
    navigate('/signup');
  };

  // Clear token + optionally redirect
  const handleLogout = () => {
    localStorage.removeItem('token');
    setToken('');
    navigate('/');
  };

  return (
    <div className="relative h-screen w-screen bg-white text-black dark:bg-black dark:text-white overflow-hidden">
      <TopBar
        token={token}
        onSignIn={handleSignIn}
        onSignUp={handleSignUp}
        onLogout={handleLogout}
      />

      <SideMenu userRole={token ? 'user' : 'guest'} onProtectedClick={handleProtectedClick} />

      <div className="pt-[60px] h-full">
        <WebSocketMap token={token} />
      </div>

      {showLoginPrompt && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-[100]">
          <div className="bg-white text-black dark:bg-zinc-900 dark:text-white p-6 rounded-xl text-center shadow-lg">
            <p className="mb-4">This feature requires signing in.</p>
            <Button onClick={() => setShowLoginPrompt(false)} color="primary">
              Close
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
