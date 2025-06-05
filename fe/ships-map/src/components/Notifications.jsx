import React, { useEffect, useState } from "react";
import { Button, Card, CardBody } from "@heroui/react";
import { ChevronDown, ChevronUp, X } from "lucide-react";
import { authFetch } from "../utils/authFetch";

export default function NotificationsTab() {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [hasNewNotifications, setHasNewNotifications] = useState(false);
  const notificationsRef = React.useRef([]);

   useEffect(() => {
    notificationsRef.current = notifications;
  }, [notifications]);


  useEffect(() => {
    let interval;

    const fetchNotifications = async () => {
    try {
        const res = await authFetch("https://localhost:8443/notification/get-all-notifications", {
        headers: {
            "Content-Type": "application/json",
        },
        });

        if (!res.ok) throw new Error("Failed to fetch notifications");

        let data = await res.json();

        // Sort by id descending
        data.sort((a, b) => b.id - a.id);

        // Keep only the 100 most recent
        if (data.length > 100) {
        data = data.slice(0, 100);
        }

        // Check for new notifications (by comparing highest ID)
        const latestFetchedId = data[0]?.id;
        const latestStoredId = notificationsRef.current[0]?.id;

        if (!isOpen && latestFetchedId && latestFetchedId !== latestStoredId) {
        setHasNewNotifications(true);
        }

        // Update the state
        setNotifications(data);
    } catch (err) {
        console.error("Error fetching notifications:", err);
    }
    };


    fetchNotifications();
    interval = setInterval(fetchNotifications, 10000);

    return () => clearInterval(interval);
  }, []);


  const toggleTab = () => {
  if (!isOpen) {
    setHasNewNotifications(false);
  }
  setIsOpen(!isOpen);
};

  const handleDeleteNotification = async (id) => {
    try {
      const res = await authFetch(`https://localhost:8443/notification/delete-notification?id=${id}`, {
        method: "DELETE",
      });

      if (!res.ok) throw new Error("Failed to delete notification");

      setNotifications((prev) => prev.filter((notif) => notif.id !== id));
      console.log(`Notification with ID ${id} deleted successfully.`);
    } catch (err) {
      console.error("Error deleting notification:", err);
    }
  };

  return (
    <div
      className={`
        fixed left-4 bottom-0 translate-y-1 z-[1300]
        w-[450px] rounded-t-xl overflow-hidden
        shadow-xl border border-gray-300 dark:border-gray-800
        transition-all duration-300 ease-in-out
        bg-neutral-100/50 dark:bg-neutral-900/50
        backdrop-blur-sm
        ${isOpen ? "h-[280px]" : "h-10"}
      `}
    >
      <Card isBlurred className="h-full w-full bg-transparent border-none">
        <CardBody className="p-0 flex flex-col h-full">
          {/* Toggle Button */}
          <Button
            variant="solid"
            color="default"
            className={`
              w-full flex justify-between items-center px-4 py-2 text-sm font-semibold
              bg-gray-50 dark:bg-neutral-950/50
              hover:bg-gray-200 dark:hover:bg-gray-800
              rounded-none
            `}
            onPress={toggleTab}
          >
            <div>
              Notifications
              {hasNewNotifications && !isOpen && (
                <span className="bg-red-500 text-white rounded-full px-1 text-xs font-bold animate-pulse ml-2">
                    !
                </span>
              )}
            </div>
            {isOpen ? <ChevronDown size={20} /> : <ChevronUp size={20} />}
          </Button>

          {/* Notifications Content */}
          {isOpen && (
            <div className="flex-1 overflow-y-auto p-3 space-y-3">
              {notifications.length === 0 ? (
                <div className="text-center text-gray-500 mt-4 italic">
                  No notifications
                </div>
              ) : (
                notifications.map((notif) => (
                  <div
                    key={notif.id}
                    className={`
                      p-2 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800
                      bg-gray-50 dark:bg-neutral-950
                      hover:bg-gray-200 dark:hover:bg-gray-800
                      transition-colors
                      flex flex-col
                    `}
                  >
                    <div className="flex justify-between items-center">
                      <span className="font-semibold text-xs text-gray-600 dark:text-gray-400">
                        {new Date(notif.datetimeCreated).toLocaleString('en-GB')}
                      </span>
                      <Button
                        isIconOnly
                        size="sm"
                        variant="light"
                        color="danger"
                        onPress={() => handleDeleteNotification(notif.id)}
                      >
                        <X size={16} />
                      </Button>
                    </div>
                    <div className="text-sm text-gray-800 dark:text-gray-100">
                      {notif.description}
                    </div>
                  </div>
                ))
              )}
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
}
