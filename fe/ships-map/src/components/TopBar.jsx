import { Input, Button, Avatar, Dropdown, DropdownItem, DropdownMenu, DropdownTrigger } from '@heroui/react';
import { Search, User } from 'lucide-react';
import ThemeSwitcher from './ThemeSwitcher';
import { useNavigate } from 'react-router-dom';


export default function TopBar({ onSignIn, onSignUp, token, onLogout }) {
  const navigate = useNavigate();
  const isGuest = !token;

  const onUserProfile = () => {
    navigate('/profile');
  };
  
  return (
    <div className="fixed top-0 left-0 right-0 z-[1100] flex items-center justify-between bg-white px-3 py-3 shadow-md h-[60px] dark:bg-black">
      {/* Logo */}
      <div className="flex gap-2 items-center">
        <img src="/logo.png" alt="logo" className="h-[160px] w-auto" />
      </div>

      {/* Search */}
      <div className="w-[300px] flex items-center justify-center">
        <Input
          size='sm'
          radius="full"
          placeholder="Search"
          endContent={<Search className="text-default-400" size={16} />}
        />
      </div>

      {/* Right Side: Theme + Auth/Profile */}
      <div className="flex gap-2 items-center">
        <ThemeSwitcher />

        {isGuest ? (
          <>
            <Button size="sm" variant="bordered" radius="full" onClick={onSignIn}>
              Sign In
            </Button>
            <Button size="sm" variant="solid" radius="full" onClick={onSignUp}>
              Sign Up
            </Button>
          </>
        ) : (
          <Dropdown placement="bottom-end">
            <DropdownTrigger>
              <Avatar isBordered size='sm' icon={<User />} className="cursor-pointer" />
            </DropdownTrigger>
            <DropdownMenu aria-label="User Menu">
              <DropdownItem key="profile" onClick={onUserProfile}>Profile</DropdownItem>
              <DropdownItem key="logout" onClick={onLogout} className="text-danger">
                Log Out
              </DropdownItem>
            </DropdownMenu>
          </Dropdown>
        )}
      </div>
    </div>
  );
}
