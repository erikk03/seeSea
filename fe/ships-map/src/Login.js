import React, { useState } from 'react';

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
    <div style={{ maxWidth: 320, margin: '2rem auto' }}>
      <h2>Sign In</h2>
      <form onSubmit={submit}>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            style={{ width: '100%', marginBottom: 8 }}
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            style={{ width: '100%', marginBottom: 8 }}
          />
        </label>
        {error && (
          <div style={{ color: 'red', marginBottom: 8 }}>
            {error}
          </div>
        )}
        <button type="submit" style={{ width: '100%' }}>
          Log In
        </button>
      </form>
    </div>
  );
}
