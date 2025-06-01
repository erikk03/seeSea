
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Card, CardBody, Divider, Button } from "@heroui/react";
import { Ship, Bell, Filter, HelpCircle, Pin } from "lucide-react";

export default function SideMenu({ userRole = "guest", activeMenu, onToggleMenu , onProtectedClick}) {
  const [isPinned, setIsPinned] = useState(false);
  const [isHovered, setIsHovered] = useState(false);
  const navigate = useNavigate();

  const isGuest = userRole === "guest";
  const isExpanded = isHovered || isPinned;

  const handleClick = (key, requiresAuth) => {
    if (requiresAuth && isGuest) {
      onProtectedClick(key);
    } else {
      onToggleMenu(key);
    }
  };

  // Handle click for Help button
  const handleHelpClick = () => {
    navigate("/help");
  };

  return (
  <Card
    isBlurred
    onMouseEnter={() => setIsHovered(true)}
    onMouseLeave={() => setIsHovered(false)}
    className={`
      fixed left-4 top-1/2 -translate-y-1/2 z-[1100]
      transition-all duration-600 ease-in-out overflow-hidden
      ${isExpanded ? "w-[150px]" : "w-[60px]"}
      bg-[#08203e]/90 dark:bg-neutral-950/50
      shadow-xl border-none
    `}
  >
    <CardBody className="p-3 text-white h-[360px] flex flex-col items-center overflow-hidden">
      {/* Top: Pin */}
      <div className="w-full flex flex-col items-center gap-3">
        <Button
          isIconOnly={!isExpanded}
          variant="light"
          onPress={() => setIsPinned(!isPinned)}
          className={`w-full flex items-center ${
            isExpanded ? "justify-start" : "justify-center"
          } text-white hover:bg-white/10`}
        >
          <Pin size={18} strokeWidth={isPinned ? 2.5 : 1.5} fill={isPinned ? "white" : "none"}/>
          {isExpanded && (
            <span className="text-sm ml-2">
              {isPinned ? "Unpin" : "Pin"}
            </span>
          )}
        </Button>
        <Divider className="bg-white/20 w-full" />
      </div>

      {/* Spacer */}
      <div className="flex-1" />

      {/* Centered Menu Buttons */}
      <div className="flex flex-col gap-3 items-center w-full">
        <Button
          isIconOnly={!isExpanded}
          variant="light"
          onPress={() => handleClick("My Fleet", true)}
          className={`
            w-full flex items-center text-white hover:bg-white/10 
            ${isExpanded ? "justify-start" : "justify-center"}
            ${activeMenu === "My Fleet" ? "bg-white/10" : "" }
          `}
        >
          <Ship size={18} />
          {isExpanded && <span className="text-sm ml-2">My Fleet</span>}
        </Button>

        <Button
          isIconOnly={!isExpanded}
          variant="light"
          onPress={() => handleClick("Alerts", true)}
          className={`
            w-full flex items-center text-white hover:bg-white/10 
            ${isExpanded ? "justify-start" : "justify-center"}
            ${activeMenu === "Alerts" ? "bg-white/10" : "" }
          `}
        >
          <Bell size={18} />
          {isExpanded && <span className="text-sm ml-2">Alerts</span>}
        </Button>

        <Button
          isIconOnly={!isExpanded}
          variant="light"
          onPress={() => handleClick("Filters", true)}
          className={`
            w-full flex items-center text-white hover:bg-white/10 
            ${isExpanded ? "justify-start" : "justify-center"}
            ${activeMenu === "Filters" ? "bg-white/10" : "" }
          `}
        >
          <Filter size={18} />
          {isExpanded && <span className="text-sm ml-2">Filters</span>}
        </Button>
      </div>

      {/* Spacer */}
      <div className="flex-1" />

      {/* Bottom: Help */}
      <div className="w-full">
        <Divider className="bg-white/20 w-full mb-3" />
        <Button
          isIconOnly={!isExpanded}
          variant="light"
          onPress={handleHelpClick} // ⬅️ Navigates to /help
          className={`w-full flex items-center ${
            isExpanded ? "justify-start" : "justify-center"
          } text-white hover:bg-white/10`}
        >
          <HelpCircle size={18} />
          {isExpanded && <span className="text-sm ml-2">Help</span>}
        </Button>
      </div>
    </CardBody>
  </Card>
);
}