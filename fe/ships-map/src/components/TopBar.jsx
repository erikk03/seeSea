import { Input, Button } from '@heroui/react';
import { Search } from 'lucide-react';
import ThemeSwitcher from './ThemeSwitcher';

export default function TopBar({ onSignIn, onSignUp }) {
  return (
    <div className="fixed top-0 left-0 right-0 z-50 flex items-center justify-between bg-white px-6 py-3 shadow-md h-[60px] dark:bg-black">
      {/* Logo */}
      <div className="flex items-center gap-2 font-semibold text-lg">
        <img src="/logo.png" alt="logo" className="h-10 w-auto" />
      </div>

      {/* Search */}
      <div className="w-[300px]">
        <Input
          radius="lg"
          placeholder="Search"
          endContent={<Search className="text-default-400" size={16} />}
          className="bg-default-100"
        />
      </div>

      {/* Right Side: Auth + Theme */}
      <div className="flex gap-2 items-center">
        <Button size="sm" variant="light" onClick={onSignIn}>Sign In</Button>
        <Button size="sm" variant="solid" color="primary" onClick={onSignUp}>Sign Up</Button>
        <ThemeSwitcher />
      </div>
    </div>
  );
}
