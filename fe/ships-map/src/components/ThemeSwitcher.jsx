import { useTheme } from '@heroui/use-theme';
import { Moon, Sun } from 'lucide-react';

export default function ThemeSwitcher() {
  const { theme, setTheme } = useTheme();

  const toggleTheme = () => {
    setTheme(theme === 'dark' ? 'light' : 'dark');
  };

  return (
    <button
      onClick={toggleTheme}
      className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition"
      title="Toggle theme"
    >
      {theme === 'dark' ? (
        <Sun className="text-yellow-400" size={18} />
      ) : (
        <Moon className="text-gray-800" size={18} />
      )}
    </button>
  );
}
