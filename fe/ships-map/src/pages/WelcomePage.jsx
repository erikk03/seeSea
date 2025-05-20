// fe/src/pages/WelcomePage.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import Logo from '../assets/logo.png';      // adjust path if needed
import '../styles/WelcomePage.css';

export default function WelcomePage() {
  const navigate = useNavigate();

  const handleEnter = () => {
    navigate('/signin');
  };

  return (
    <div className="welcome-page">
      <img src={Logo} alt="seeSea logo" className="welcome-logo" />
      <button className="btn-enter" onClick={handleEnter}>
        →
      </button>
    </div>
  );
}
