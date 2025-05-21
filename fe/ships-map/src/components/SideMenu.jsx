import { Button } from "@heroui/react";
import { Ship, Bell, Filter, HelpCircle } from "lucide-react";

const menuItems = [
  { label: "My Fleet", icon: Ship, requiresAuth: true },
  { label: "Alerts", icon: Bell, requiresAuth: true },
  { label: "Filters", icon: Filter, requiresAuth: true },
];

export default function SideMenu({ userRole = "guest", onProtectedClick }) {
  const handleClick = (label, requiresAuth) => {
    if (requiresAuth && userRole === "guest") {
      onProtectedClick(label);
    } else {
      console.log(`Accessing ${label}`);
    }
  };

  return (
    <div className="fixed left-4 top-1/2 -translate-y-1/2 z-[1100] flex flex-col justify-between w-[150px] h-[280px] bg-[#004368] text-white rounded-2xl shadow-xl p-4">
      <div className="flex flex-col gap-2">
        {menuItems.map(({ label, icon: Icon, requiresAuth }) => (
          <Button
            key={label}
            variant="light"
            onPress={() => handleClick(label, requiresAuth)}
            className="flex items-center justify-start gap-2 text-white hover:bg-white/10 px-3 py-2 rounded-lg"
          >
            <Icon size={18} />
            <span className="text-sm">{label}</span>
          </Button>
        ))}
      </div>

      <Button
        variant="light"
        onPress={() => handleClick("Help", false)}
        className="flex items-center justify-start gap-2 text-white hover:bg-white/10 px-3 py-2 rounded-lg"
      >
        <HelpCircle size={18} />
        <span className="text-sm">Help</span>
      </Button>
    </div>
  );
}
