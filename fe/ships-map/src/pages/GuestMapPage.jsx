import React, { useState } from 'react';
import WebSocketMap from '../features/map/WebSocketMap';
import SideMenu from '../components/SideMenu';
import TopBar from '../components/TopBar';
import { Input, Button } from '@heroui/react';
import { Search } from 'lucide-react';

export default function GuestMapPage() {
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);

  const handleProtectedClick = (label) => {
    console.log(`"${label}" clicked, but guest access. Prompting login.`);
    setShowLoginPrompt(true);
  };

  return (
    <div className="relative h-screen w-screen bg-white overflow-hidden">
      {/* Top Bar */}
      <TopBar
        onSignIn={() => console.log('Sign In clicked')}
        onSignUp={() => console.log('Sign Up clicked')}
      />

      {/* Sidebar */}
      <SideMenu userRole="guest" onProtectedClick={handleProtectedClick} />

      {/* Map Container */}
      <div className="pt-[60px] h-full">
        <WebSocketMap token="" />
      </div>

      {/* Login Prompt */}
      {showLoginPrompt && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-[100]">
          <div className="bg-white p-6 rounded-xl text-center">
            <p className="mb-4">This feature requires signing in.</p>
            <Button onPress={() => setShowLoginPrompt(false)} color="primary">
              Close
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
