import React, { useState } from 'react';
import loginImage from '../../assets/login/login_ships.png';


export default function Login({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const submit = async e => {
    e.preventDefault();
    setError('');
    try {
      const res = await fetch('https://localhost:8443/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      if (!res.ok) {
        const body = await res.json();
        throw new Error(body.message || res.statusText);
      }
      const { token } = await res.json();
      onLogin(token);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div
    style={{
      backgroundColor: '#003C62',
      width: '100vw',
      height: '100vh',
      overflow: 'hidden',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      position: 'relative', 
      padding: '2rem',
    }}
  >
      <h1 style={{ color: 'white', marginBottom: '2rem', textAlign: 'center' }}>
    Sign in for <span style={{ color: 'gold' }}>premium</span> access!
  </h1>
   
    <div
      style={{
        maxWidth: 400,
        width: '100%',
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '10px',
        boxShadow: '0 6px 20px rgba(0, 0, 0, 0.15)',
        boxSizing: 'border-box',
        zIndex: 2
      }}
    >
     
    
        <form onSubmit={submit}>
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Email</label>
            <input
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '0.5rem',
                borderRadius: 4,
                border: '1px solid #ccc',
                boxSizing: 'border-box'
              }}
            />

          </div>
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: 4 }}>Password</label>
           <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '0.5rem',
                borderRadius: 4,
                border: '1px solid #ccc',
                boxSizing: 'border-box'
              }}
            />

          </div>
          {error && (
            <div style={{ color: 'red', marginBottom: '1rem' }}>{error}</div>
          )}
          <button
            type="submit"
            style={{
              width: '100%',
              padding: '0.75rem',
              backgroundColor: '#003C62',
              color: 'white',
              border: 'none',
              borderRadius: 4,
              fontWeight: 'bold'
            }}
          >
            Sign In
          </button>
         <div style={{ marginTop: '1rem' }}>
          <a
            href="/forgot-password"
            style={{
              color: '#003C62',
              textDecoration: 'none',
              fontWeight: '500',
              fontSize: '0.9rem',
              display: 'inline-block'
            }}
            onMouseEnter={e => (e.target.style.textDecoration = 'underline')}
            onMouseLeave={e => (e.target.style.textDecoration = 'none')}
          >
            Forgot password?
          </a>
        </div>
        </form>
       <p style={{ color: '#003C62', marginTop: '1rem', textAlign: 'center' }}>
        Are you new here?{' '}
        <a href="/register" style={{ color: '#61dafb', textDecoration: 'underline' }}>
          Create an account
        </a>{' '}
        for free!
      </p>
    </div>

    <img
      src={loginImage}
      alt="Login visual"
      style={{
        position: 'absolute',
        right: 0,
        top: 0,
        height: '100%',
        width: 'auto',
        objectFit: 'cover',
        zIndex: 1,
        borderTopLeftRadius: '10px',
        borderBottomLeftRadius: '10px'
      }}
    />
  </div>
);
}