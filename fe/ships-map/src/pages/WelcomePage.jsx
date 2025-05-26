// fe/src/pages/WelcomePage.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import Logo from '../assets/logo.png';      // adjust path if needed
import '../styles/WelcomePage.css';
import {LogIn} from 'lucide-react';

export default function WelcomePage() {
  const navigate = useNavigate();

  const handleEnter = () => {
    navigate('/guest-map');
  };

  return (
    <div className="welcome-page">
      <img src={Logo} alt="seeSea logo" className="welcome-logo" />
      <button className="btn-enter" onClick={handleEnter}>
        <LogIn/>
      </button>
    </div>
  );
}
