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
  const [hasActiveFilters, setHasActiveFilters] = useState(false);
  const [filteredShips, setFilteredShips] = useState(null);
  const [selectedFilters, setSelectedFilters] = useState(null);

  const handleProtectedClick = (label) => {
    console.log(`"${label}" clicked, but guest access. Prompting login.`);
  };

  const toggleMenu = (menuKey) => {
    setActiveMenu(prev => (prev === menuKey ? null : menuKey));
  }

  const clearFilters = () => {
    setHasActiveFilters(false);
    setFilteredShips(null);
    setSelectedFilters(null);
  }

  const handleFiltersChange = async (filters) => {
    const hasFilters =
      filters.vesselTypeIds?.length > 0 ||
      filters.vesselStatusIds?.length > 0 ||
      (filters.filterFrom && filters.filterFrom !== "All");

    setHasActiveFilters(hasFilters);
    setSelectedFilters(filters);

    try {
      const token = localStorage.getItem("token");

      const res = await fetch("https://localhost:8443/vessel/set-filters-and-get-map", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          filterFrom: filters.filterFrom,
          vesselStatusIds: filters.vesselStatusIds || [],
          vesselTypeIds: filters.vesselTypeIds || [],
        }),
      });

      if (!res.ok) throw new Error("Failed to fetch filtered ships");

      const data = await res.json();
      setFilteredShips(data); // Update your map data
    } catch (err) {
      console.error("Error fetching filtered vessels:", err);
    }
  };


    

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
        <WebSocketMap token={token} vessels={filteredShips} />
      </div>

      {activeMenu === 'Filters' && (
        <FiltersMenu
          onFiltersChange={handleFiltersChange}
          onClearFilters={clearFilters}
        />
      )}

      {hasActiveFilters && (
      <div className="fixed top-1/2 translate-y-48 left-4 z-[1200]">
        <div className="flex items-center bg-black text-white text-sm px-3 py-1 rounded-lg shadow-md gap-2">
          <span>Filters</span>
          <button
            onClick={clearFilters}
            className="hover:text-red-400 text-white font-bold"
          >
            ×
          </button>
        </div>
      </div>
    )}

    </div>
  );
}
