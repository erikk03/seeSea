import { Button, Avatar, Dropdown, DropdownItem, DropdownMenu, DropdownTrigger, Autocomplete, AutocompleteItem } from '@heroui/react';
import { User, SearchIcon } from 'lucide-react';
import ThemeSwitcher from './ThemeSwitcher';
import { useNavigate } from 'react-router-dom';


export default function TopBar({ onSignIn, onSignUp, token, onLogout, ships= [], onShipSelect }) {
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
        <Autocomplete
          size="sm"
          radius="full"
          variant='bordered'
          aria-label='Search vessels by MMSI'
          placeholder="Search vessel by MMSI"
          className="w-[300px]"
          startContent={<SearchIcon className="text-gray-500" />}
          onSelectionChange={(mmsi) => {
            const selected = ships.find(s => s.mmsi === mmsi);
            if (selected) {
              onShipSelect?.(selected); // Notify map for popup
            }
          }}
        >
          {ships.map((ship) => (
            <AutocompleteItem key={ship.mmsi} textValue={ship.mmsi}>
              MMSI: {ship.mmsi} — {ship.vesselType || 'Unknown'}
            </AutocompleteItem>
          ))}
        </Autocomplete>
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
