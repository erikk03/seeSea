import React, { useState, useEffect } from 'react';
import Login from './features/auth/Login';
import WebSocketMap from './features/map/WebSocketMap';

export default function App() {
  const [token, setToken] = useState(() => localStorage.getItem('token') || '');

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }, [token]);

  const handleLogin = jwt => {
    setToken(jwt);
  };

  const handleLogout = () => {
    setToken('');
  };

  // If no token yet, show login form
  if (!token) {
    return <Login onLogin={handleLogin} />;
  }

  // Otherwise show the map
  return <WebSocketMap token={token} onLogout={handleLogout} />;
}
