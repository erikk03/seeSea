import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import WelcomePage from './pages/WelcomePage';
import Login from './features/auth/Login';
import GuestMapPage from './pages/GuestMapPage';

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
    localStorage.removeItem('token');
  };

  return (
    <Router>
      <Routes>
        {/* Public Welcome Page */}
        <Route path="/" element={<WelcomePage />} />

        {/* Login Route */}
        <Route
          path="/signin"
          element={
            !!token ? <Navigate to="/map" /> : <Login onLogin={handleLogin} />
          }
        />

        {/* Map Route */}
        <Route path="/map" element={<GuestMapPage />}/>

        {/* Redirect unknown routes to welcome */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </Router>
  );
}
