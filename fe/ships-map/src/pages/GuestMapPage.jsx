import React, { useState } from 'react';
import WebSocketMap from '../features/map/WebSocketMap';
import SideMenu from '../components/SideMenu';
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
      <div className="fixed top-0 left-0 right-0 z-50 flex items-center justify-between bg-white px-6 py-3 shadow-md h-[60px]">
        {/* Logo */}
        <div className="flex items-center gap-2 font-semibold text-lg">
          <img src="/logo.png" alt="logo" className="h-20 w-50" />
        </div>

        {/* Search */}
        <div className="w-[300px]">
          <Input
            radius="lg"
            placeholder="Search"
            endContent={<Search className="text-default-400" size={16} />}
            className="bg-default-100"
          />
        </div>

        {/* Auth Buttons */}
        <div className="flex gap-2">
          <Button size="sm" variant="light">Sign In</Button>
          <Button size="sm" variant="solid" color="primary">Sign Up</Button>
        </div>
      </div>

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
