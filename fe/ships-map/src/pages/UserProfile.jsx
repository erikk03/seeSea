import React, { useEffect, useState } from 'react';
import { ArrowLeft, Pencil } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { authFetch } from '../utils/authFetch';
import TopBar from '../components/TopBar';
import {
  Card,
  CardHeader,
  CardBody,
  Input,
  Button,
  Avatar,
  Modal,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Divider,
  user,
} from '@heroui/react';

export default function UserProfile({ token, onLogout, setToken }) {
  const [userInfo, setUserInfo] = useState(null);
  const [error, setError] = useState('');
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [status, setStatus] = useState({ success: null, message: '' });
  const [confirmPassword, setConfirmPassword] = useState('');
  const [newUsername, setNewUsername] = useState('');

  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [showUsernameModal, setShowUsernameModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const navigate = useNavigate();

  const onLeftArrowClick = () => {
    if (token) {
      navigate('/registered-map');
    } else {
      navigate('/guest-map');
    }
  };

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const res = await authFetch('https://localhost:8443/registered-user/get-user-info', {
          method: 'GET',
        });

        if (!res.ok) throw new Error('Failed to fetch user info');

        const data = await res.json();
        setUserInfo(data);
      } catch (err) {
        console.error('User info fetch error:', err);
        setStatus({ success: false, message: err.message || 'Failed to load user info' });
      }
    };

    fetchUserInfo();
  }, [token]);

  const handleUsernameChange = async () => {
    if (!newUsername || !confirmPassword) return;

    try {
      const res = await authFetch('https://localhost:8443/registered-user/change-username', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: newUsername,
          password: confirmPassword,
        }),
      });

      if (!res.ok) throw new Error('Username update failed');

      const { token: newToken } = await res.json();

      // Update token in localStorage
      localStorage.setItem('token', newToken);
      setToken(newToken);

      setStatus({ success: true, message: 'Username updated successfully' });
      setNewUsername('');
      setConfirmPassword('');
      setShowUsernameModal(false);

      // Fetch updated user info with the new token
      const updatedRes = await authFetch('https://localhost:8443/registered-user/get-user-info', {
        method: 'GET',
      });

      if (updatedRes.ok) {
        const updatedUser = await updatedRes.json();
        setUserInfo(updatedUser);
      }

    } catch (err) {
      console.error('Username change error:', err);
      setStatus({ success: false, message: err.message || 'Failed to update username' });
    }
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const res = await authFetch('https://localhost:8443/registered-user/change-password', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ oldPassword, newPassword }),
      });

      if (!res.ok) {
        const body = await res.json();
        throw new Error(body.message || body.error || res.statusText);
      }

      setStatus({ success: true, message: 'Password updated successfully' });
      setOldPassword('');
      setNewPassword('');
      setShowPasswordModal(false);
    } catch (err){
      setStatus({ success: false, message: err.message || err.error || 'Password change failed' });
    }
  };

  const handleDeleteAccount = async (password) => {
    try {
      const res = await authFetch(`https://localhost:8443/registered-user/delete-user?password=${encodeURIComponent(password)}`, {
        method: 'DELETE',
      });

      if (!res.ok) throw new Error('Account deletion failed');

      onLogout(); // clear token and redirect
      navigate('/');
    } catch (err) {
      console.error('Delete error:', err);
      setStatus({ success: false, message: 'Failed to delete account' });
    }
  };

  return (
    <div className="relative min-h-screen bg-neutral-200 text-black dark:bg-gray-800 dark:text-white flex justify-center items-start p-6">
      <TopBar token={token} onLogout={onLogout} />

      <Button
        isIconOnly
        variant="light"
        onClick={onLeftArrowClick}
        className="absolute top-20 left-4 z-10"
        aria-label="Back to map"
      >
        <ArrowLeft />
      </Button>

      <Card className="w-full max-w-2xl mt-16">
        <CardHeader className="flex justify-between items-center gap-4">
          <div className="flex items-center gap-4">
            <Avatar
              showFallback
              src="https://images.unsplash.com/broken"
              isBordered
              className="h-16 w-16 text-base"
            />
            <div>
              <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
                {userInfo?.username || 'Loading...'}
              </h2>
              <p className="text-sm text-gray-500 dark:text-gray-400">{userInfo?.email}</p>
              <p className="text-xs text-gray-400 mt-1">{userInfo?.role}</p>
            </div>
          </div>

          <Button
            onClick={() => setShowUsernameModal(true)}
            size="sm"
            color="default"
            variant='bordered'
            className="-translate-y-4"
            startContent={<Pencil size={16} />}
          >
            Change Username
          </Button>
        </CardHeader>


        <Divider className='m-2'/>

        <CardBody className="flex flex-col items-center gap-4">
          <Button onClick={() => setShowPasswordModal(true)} className="w-fit px-4">
            Change Password
          </Button>
          
          {userInfo?.role !== 'Administrator' && (
            <Button onClick={() => setShowDeleteModal(true)} className="w-fit px-4" color="danger" variant="bordered">
              Delete Account
            </Button>
          )}
          
          {status.message && (
            <p className={`mt-4 text-sm ${status.success ? 'text-green-600' : 'text-red-600'}`}>
              {status.message}
            </p>
          )}
        </CardBody>
      </Card>

      {/* Password Modal */}
      <Modal isOpen={showPasswordModal} onClose={() => setShowPasswordModal(false)}>
        <ModalContent>
          <form onSubmit={handlePasswordChange}>
            <ModalHeader>Change Password</ModalHeader>
            <ModalBody>
              <Input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="New Password"
                required
              />
              <p className="mt-4 mb-1 text-sm">Please enter your password to confirm:</p>
              <Input
                type="password"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
                placeholder="Current Password"
                required
              />
              {status.message && (
                <p className={`text-sm mt-2 ${status.success ? 'text-green-600' : 'text-red-600'}`}>
                  {status.message}
                </p>
              )}
            </ModalBody>
            <ModalFooter>
              <Button variant="light" onClick={() => setShowPasswordModal(false)}>
                Cancel
              </Button>
              <Button type="submit" color='primary' isDisabled={!oldPassword || !newPassword}>
                Change
              </Button>
            </ModalFooter>
          </form>
        </ModalContent>
      </Modal>

      {/* Username Modal (Placeholder) */}
      <Modal isOpen={showUsernameModal} onClose={() => {
        setShowUsernameModal(false);
        setConfirmPassword('');
        setNewUsername('');
      }}>
        <ModalContent>
          <ModalHeader>Change Username</ModalHeader>
          <ModalBody>
            <Input
              type="text"
              placeholder="New Username"
              value={newUsername}
              onChange={(e) => setNewUsername(e.target.value)}
              required
            />
            <p className="mt-4 text-sm">Please enter your password to confirm:</p>
            <Input
              type="password"
              placeholder="Current Password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
            {status.message && (
              <p className={`text-sm mt-2 ${status.success ? 'text-green-600' : 'text-red-600'}`}>
                {status.message}
              </p>
            )}
          </ModalBody>
          <ModalFooter>
            <Button variant="light" onClick={() => {
              setShowUsernameModal(false);
              setConfirmPassword('');
              setNewUsername('');
            }}>
              Cancel
            </Button>
            <Button
              color="primary"
              isDisabled={!newUsername || !confirmPassword}
              onClick={handleUsernameChange}
            >
              Update
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>


      {/* Delete Modal */}
      <Modal isOpen={showDeleteModal} onClose={() => setShowDeleteModal(false)}>
        <ModalContent>
          <ModalHeader>Delete Account</ModalHeader>
          <ModalBody>
            <p className="text-red-600 font-medium mb-2">
              Are you sure you want to permanently delete your account?
            </p>
            <p className="text-sm">Please enter your password to confirm:</p>
            <Input
              type="password"
              placeholder="Password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
            {status.message && (
              <p className={`text-sm mt-2 ${status.success ? 'text-green-600' : 'text-red-600'}`}>
                {status.message}
              </p>
            )}
          </ModalBody>
          <ModalFooter>
            <Button variant="light" onClick={() => {
              setShowDeleteModal(false);
              setConfirmPassword('');
            }}>
              Cancel
            </Button>
            <Button
              color="danger"
              isDisabled={!confirmPassword}
              onClick={() => {
                handleDeleteAccount(confirmPassword);
                setConfirmPassword('');
              }}
            >
              Delete
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </div>
  );
}
