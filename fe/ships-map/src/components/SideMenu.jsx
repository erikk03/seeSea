import React from 'react';

export default function SideMenu({ userRole = 'guest', onProtectedClick }) {
  // Define which items require auth
  const items = [
    { label: 'My Fleet', requiresAuth: true },
    { label: 'Alerts', requiresAuth: true },
    { label: 'Filters', requiresAuth: true },
    { label: 'Help', requiresAuth: false },
  ];

  const handleClick = (item) => {
    if (item.requiresAuth && userRole === 'guest') {
      onProtectedClick(item.label); // trigger login prompt
    } else {
      console.log(`Accessing ${item.label}`);
      // You can route or open panel here
    }
  };

  return (
    <div style={{
      width: '200px',
      backgroundColor: '#1e3a5f',
      color: '#fff',
      padding: '1rem',
      display: 'flex',
      flexDirection: 'column',
      gap: '1rem'
    }}>
      {items.map(item => (
        <button
          key={item.label}
          onClick={() => handleClick(item)}
          style={{
            background: 'transparent',
            border: 'none',
            color: 'white',
            cursor: 'pointer',
            fontSize: '16px',
            textAlign: 'left'
          }}
        >
          {item.label}
        </button>
      ))}
    </div>
  );
}
