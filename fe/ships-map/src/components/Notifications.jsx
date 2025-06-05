import React, { useEffect, useState } from "react";
import { Button, Card, CardBody, Divider } from "@heroui/react";
import { ChevronDown, ChevronUp, Trash2 } from "lucide-react";

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
        const token = localStorage.getItem("token");
        const res = await fetch("https://localhost:8443/notification/get-all-notifications", {
        headers: {
            Authorization: `Bearer ${token}`,
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
        console.log("Latest fetched ID:", latestFetchedId);
        const latestStoredId = notificationsRef.current[0]?.id;
        console.log("Latest stored ID:", latestStoredId);

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
      const token = localStorage.getItem("token");
      const res = await fetch(`https://localhost:8443/notification/delete-notification?id=${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
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
        fixed left-4 bottom-4 z-[1100]
        w-80 rounded-xl overflow-hidden
        shadow-xl border border-gray-300
        transition-all duration-300 ease-in-out
        bg-white/90 dark:bg-gray-900/90
        backdrop-blur-sm
        ${isOpen ? "h-[360px]" : "h-12"}
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
              bg-gray-100/80 dark:bg-gray-800/80
              hover:bg-gray-200 dark:hover:bg-gray-700
              rounded-none
            `}
            onPress={toggleTab}
          >
            {hasNewNotifications && !isOpen && (
                <span className="bg-red-500 text-white rounded-full px-1 text-xs font-bold animate-pulse mr-2">
                    !
                </span>
                )}
                Notifications
            {isOpen ? <ChevronDown size={20} /> : <ChevronUp size={20} />}
          </Button>

          {/* Notifications Content */}
          {isOpen && (
            <div className="flex-1 overflow-y-auto p-2 space-y-2">
              {notifications.length === 0 ? (
                <div className="text-center text-gray-500 mt-4 italic">
                  No notifications
                </div>
              ) : (
                notifications.map((notif) => (
                  <div
                    key={notif.id}
                    className={`
                      p-3 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700
                      bg-gray-50 dark:bg-gray-800
                      hover:bg-gray-100 dark:hover:bg-gray-700
                      transition-colors
                      flex flex-col gap-1
                    `}
                  >
                    <div className="flex justify-between items-center">
                      <span className="font-semibold text-xs text-gray-600 dark:text-gray-400">
                        ID: {notif.id}
                      </span>
                      <Button
                        isIconOnly
                        size="sm"
                        variant="light"
                        color="danger"
                        onPress={() => handleDeleteNotification(notif.id)}
                      >
                        <Trash2 size={16} />
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
