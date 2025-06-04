// components/SignupForm.jsx
import React, { useState } from 'react';
import { Button, Input } from '@heroui/react';
import { Eye, EyeOff } from 'lucide-react';

export default function SignupForm({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

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
        throw new Error(body.message || body.error || res.statusText);
      }

      try {
          const res = await fetch('https://localhost:8443/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, password }),
        });
        if (!res.ok) {
          const body = await res.json();
          throw new Error(body.message || body.error || res.statusText);
        }
        const { token } = await res.json();
        onLogin(token);
      }
      catch (err) { 
        setError(err.message);
      }
      
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <form onSubmit={submit} className="space-y-3 max-w-md m-5">
      <div className="space-y-10">
        <Input
          label="Email"
          labelPlacement="outside"
          placeholder="Enter your email"
          type="email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          variant="bordered"
          required
        />

        <Input
          label="Password"
          labelPlacement="outside"
          placeholder="Enter your password"
          type={showPassword ? 'text' : 'password'}
          value={password}
          onChange={e => setPassword(e.target.value)}
          variant="bordered"
          required
          endContent={
            <button
              type="button"
              onClick={() => setShowPassword(prev => !prev)}
              aria-label="Toggle password visibility"
              className="focus:outline-none"
            >
              {showPassword ? (
                <EyeOff className="w-5 h-5 text-default-400" />
              ) : (
                <Eye className="w-5 h-5 text-default-400" />
              )}
            </button>
          }
        />
      </div>

      {error && <div className="text-red-600 text-sm mt-4">{error}</div>}

      <Button
        type="submit"
        className="w-full mt-6 bg-[#003C62] text-white font-bold rounded-md"
      >
        Sign Up
      </Button>

      <div className="mt-4 h-5 invisible">placeholder</div>
    </form>
  );
}