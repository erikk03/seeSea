// components/SignupForm.jsx
import React, { useState } from 'react';
import { Button } from '@heroui/react';

export default function SignupForm({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const submit = async e => {
    e.preventDefault();
    setError('');
    try {
      const res = await fetch('https://localhost:8443/auth/register', {
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
    <form onSubmit={submit}>
      <div className="mb-4">
        <label className="block mb-1 text-sm font-medium text-gray-700">Email</label>
        <input
          type="email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm"
        />
      </div>
      <div className="mb-4">
        <label className="block mb-1 text-sm font-medium text-gray-700">Password</label>
        <input
          type="password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm"
        />
      </div>
      {error && <div className="text-red-600 text-sm mb-4">{error}</div>}
      <Button
        type="submit"
        className="w-full bg-[#003C62] text-white font-bold rounded-md"
      >
        Sign Up
      </Button>

      <div className="mt-4 h-5 invisible">placeholder</div>
    </form>
  );
}
