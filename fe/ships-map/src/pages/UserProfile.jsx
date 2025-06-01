import React, { useEffect, useState } from 'react';
import {
  Card,
  CardHeader,
  CardBody,
  Input,
  Button,
  Avatar,
} from '@heroui/react';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

import TopBar from '../components/TopBar';

export default function UserProfile({token, onLogout}) {
  const [userInfo, setUserInfo] = useState(null);
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [status, setStatus] = useState({ success: null, message: '' });
  
  const navigate = useNavigate();
  const onLeftArrowClick = () => {
    if (token) {
      navigate('/registered-map');
    } else {
      navigate('/map');
    }
  };

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const res = await fetch('https://localhost:8443/registered-user/get-user-info', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error('Failed to fetch user info');

        const data = await res.json();
        setUserInfo(data);
      } catch (err) {
        console.error('User info fetch error:', err);
        setStatus({ success: false, message: 'Failed to load user info' });
      }
    };

    fetchUserInfo();
  }, []);

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    if (!oldPassword || !newPassword) return;

    try {
      const res = await fetch('https://localhost:8443/registered-user/change-password', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          oldPassword,
          newPassword,
        }),
      });

      if (!res.ok) throw new Error('Password update failed');

      setStatus({ success: true, message: 'Password updated successfully' });
      setOldPassword('');
      setNewPassword('');
    } catch {
      setStatus({ success: false, message: 'Password change failed' });
    }
  };

  return (
    <div className="relative min-h-screen bg-neutral-200 text-black dark:bg-gray-800 dark:text-white flex justify-center items-start p-6">
      <TopBar
        token={token}
        onLogout={onLogout}
      />

      <Button
        isIconOnly
        variant="light"
        onClick={onLeftArrowClick}
        className="absolute top-20 left-4 z-10"
        aria-label="Back to map"
      >
        <ArrowLeft />
      </Button>

      <Card className='w-full max-w-2xl mt-16'>
        <CardHeader className="flex items-center gap-4">
          <Avatar showFallback src="https://images.unsplash.com/broken" isBordered className="h-16 w-16 text-base" />
          <div>
            <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
              {userInfo?.username || 'Loading...'}
            </h2>
            <p className="text-sm text-gray-500 dark:text-gray-400">{userInfo?.email}</p>
            <p className="text-xs text-gray-400 mt-1">{userInfo?.role}</p>
          </div>
        </CardHeader>

        <CardBody>
          <form onSubmit={handlePasswordChange} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Current Password
              </label>
              <Input
                type="password"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
                placeholder="Enter current password"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                New Password
              </label>
              <Input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Enter new password"
                required
              />
            </div>
            <Button type="submit" className="w-full">
              Change Password
            </Button>
          </form>

          {status.message && (
            <p className={`mt-4 text-sm ${status.success ? 'text-green-600' : 'text-red-600'}`}>
              {status.message}
            </p>
          )}
        </CardBody>
      </Card>
    </div>
  );
}