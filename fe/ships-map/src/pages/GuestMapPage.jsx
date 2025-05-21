import React, { useState } from 'react';
import WebSocketMap from '../features/map/WebSocketMap';
import SideMenu from '../components/SideMenu';

export default function GuestMapPage() {
  const [showLoginPrompt, setShowLoginPrompt] = useState(false);

  const handleProtectedClick = (label) => {
    console.log(`"${label}" clicked, but guest access. Prompting login.`);
    setShowLoginPrompt(true);
  };

  return (
    <div style={{ display: 'flex', height: '100vh' }}>
      <SideMenu userRole="guest" onProtectedClick={handleProtectedClick} />

      <div style={{ flex: 1 }}>
        <WebSocketMap token={''} />
      </div>

      {showLoginPrompt && (
        <div style={{
          position: 'fixed',
          top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.6)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}>
          <div style={{
            background: '#fff',
            padding: '2rem',
            borderRadius: '10px',
            textAlign: 'center'
          }}>
            <p>This feature requires signing in.</p>
            <button onClick={() => setShowLoginPrompt(false)}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
}
