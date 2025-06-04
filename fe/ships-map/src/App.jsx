import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { authFetch } from './utils/authFetch';

import WelcomePage from './pages/WelcomePage';
import Login from './features/auth/Login';
import GuestMapPage from './pages/GuestMapPage';
import Signup from './features/auth/SignUp';
import RegisteredMapPage from './pages/RegisteredMapPage';
import Help from './pages/Help';
import UserProfile from './pages/UserProfile';

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
            !!token ? <Navigate to="/guest-map" /> : <Login onLogin={handleLogin} />
          }
        />

        <Route
          path="/register"
          element={
            !!token ? <Navigate to="/guest-map" /> : <Signup onLogin={handleLogin} />
          }
        />

        {/* Map Route */}
       <Route
          path="/guest-map"
          element={
            token && token.trim() !== ''
              ? <Navigate to="/registered-map" />
              : <GuestMapPage token={token} onLogout={handleLogout} onLogin={handleLogin} />
          }
        />

        <Route path="/registered-map" 
          element={ 
            !token ? <Navigate to ="/" /> : <RegisteredMapPage token={token} onLogout={handleLogout} />} />

        {/* User Profile Route */}
        <Route
          path="/profile"
          element={
            !token ? (
              <Navigate to="/" />
            ) : (
              <UserProfile token={token} onLogout={handleLogout} setToken={setToken}/>
            )
          }
        />

        {/* Help Page */}
        <Route path="/help" element={<Help />} />
        
        {/* Redirect unknown routes to welcome */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </Router>
  );
}
