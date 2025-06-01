import { Button, Avatar, Dropdown, DropdownItem, DropdownMenu, DropdownTrigger, Autocomplete, AutocompleteItem } from '@heroui/react';
import { User, SearchIcon, UserCog, LogOut } from 'lucide-react';
import ThemeSwitcher from './ThemeSwitcher';
import { useNavigate } from 'react-router-dom';


export default function TopBar({ onSignIn, onSignUp, token, onLogout, ships= [], onShipSelect }) {
  const navigate = useNavigate();
  const isGuest = !token;

  const onUserProfile = () => {
    navigate('/profile');
  };

  const onLogoClick = () => {
    if (token) {
      navigate('/registered-map');
    } else {
      navigate('/guest-map');
    }
  }
  
  return (
    <div className="fixed top-0 left-0 right-0 z-[1100] flex items-center justify-between bg-neutral-100 px-3 py-3 shadow-md h-[60px] dark:bg-neutral-950 transition-colors duration-300">
        <div className="flex gap-2 items-center">
          <img
            src="/logo.png"
            alt="logo"
            className="h-[30px] w-auto cursor-pointer overflow-hidden"
            onClick={onLogoClick}
          />
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
              <DropdownItem key="profile" onClick={onUserProfile} endContent={<UserCog className="text-gray-500 size-5" />}>
                Profile
              </DropdownItem>
              <DropdownItem key="logout" onClick={onLogout} className="text-danger" endContent={<LogOut className="text-danger size-5" />}>
                Log Out
              </DropdownItem>
            </DropdownMenu>
          </Dropdown>
        )}
      </div>
    </div>
  );
}
