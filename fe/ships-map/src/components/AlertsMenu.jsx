import React, { useState } from "react";
import {
  Card,
  CardHeader,
  CardBody,
  Divider,
  Input,
  Switch,
  Button,
} from "@heroui/react";
import { Trash2 } from "lucide-react";
import { useEffect } from "react";

export default function AlertsMenu({ alerts, onAlertsChange, onStartZoneSelection, onRemoveZone, zone, zoneDrawing, onCancelZoneDrawing}) {
  const [speedThreshold, setSpeedThreshold] = useState(alerts?.speedThreshold || "");
  const [enterZoneEnabled, setEnterZoneEnabled] = useState(alerts?.enterZoneEnabled || false);
  const [exitZoneEnabled, setExitZoneEnabled] = useState(alerts?.exitZoneEnabled || false);


  // Fetch current alerts from the backend when the component loads
  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await fetch("https://localhost:8443/zone-of-interest/get-zone-options", {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        });
        if (!res.ok) throw new Error("Failed to fetch alert options");
        const data = await res.json();

        // Update local state with fetched data
        setSpeedThreshold(data.maxSpeed ?? "");
        setEnterZoneEnabled(data.entersZone ?? false);
        setExitZoneEnabled(data.exitsZone ?? false);

        // Notify parent about the fetched data
        onAlertsChange?.({
          speedThreshold: data.maxSpeed ?? "",
          enterZoneEnabled: data.entersZone ?? false,
          exitZoneEnabled: data.exitsZone ?? false
        });
      } catch (err) {
        console.error("Error fetching alert options:", err);
    }
    };

    fetchAlerts();
  }, []); // run only on load

  const handleSpeedChange = (e) => {
    const value = e.target.value;
    setSpeedThreshold(value);
    onAlertsChange?.({ speedThreshold: value, enterZoneEnabled, exitZoneEnabled });
  };

  const handleEnterToggle = (val) => {
    setEnterZoneEnabled(val);
    onAlertsChange?.({ speedThreshold, enterZoneEnabled: val, exitZoneEnabled });
  };

  const handleExitToggle = (val) => {
    setExitZoneEnabled(val);
    onAlertsChange?.({ speedThreshold, enterZoneEnabled, exitZoneEnabled: val });
  };

  const clearAlerts = () => {
    setSpeedThreshold("");
    setEnterZoneEnabled(false);
    setExitZoneEnabled(false);
    onAlertsChange?.({ speedThreshold: "", enterZoneEnabled: false, exitZoneEnabled: false });
  };

  return (
    <Card
      isBlurred
      className="fixed right-4 top-1/2 -translate-y-1/2 z-[1100]
      transition-all duration-600 ease-in-out overflow-hidden
      w-[240px] bg-white/90 dark:bg-black/90
      shadow-xl border-none"
    >
      <CardHeader className="text-lg font-bold px-4 pt-4 flex items-center justify-center">
        Alerts
      </CardHeader>

      <CardBody className="p-4 flex flex-col gap-4">
          <Divider />
        
        <Button
        variant="solid"
        color={zone ? "danger" : "primary"}
        className="w-full text-sm font-semibold"
        onPress={zone ? onRemoveZone : onStartZoneSelection}
        >
        {zone ? "Remove Zone of Interest" : "Set Zone of Interest"}
        </Button>

        {zoneDrawing && (
            <Button
                variant="solid"
                color="warning"
                className="w-full text-sm font-semibold"
                onPress={onCancelZoneDrawing}
            >
                Cancel Drawing
            </Button>
        )}


        <div className="flex flex-col gap-2">
          <label className="text-sm font-medium">Speed Threshold (kn)</label>
          <Input
            type="number"
            min="0"
            placeholder="Enter speed threshold"
            value={speedThreshold}
            onChange={handleSpeedChange}
            size="sm"
            className="text-sm"
          />
        </div>

        <Divider />

        <div className="flex flex-col gap-4">
          <div className="flex justify-between items-center">
            <span className="text-sm font-medium">Enter Zone</span>
            <Switch
              size="sm"
              isSelected={enterZoneEnabled}
              onValueChange={handleEnterToggle}
            />
          </div>

          <div className="flex justify-between items-center">
            <span className="text-sm font-medium">Exit Zone</span>
            <Switch
              size="sm"
              isSelected={exitZoneEnabled}
              onValueChange={handleExitToggle}
            />
          </div>
        </div>

        <Divider />

        <Button
          variant="light"
          color="danger"
          startContent={<Trash2 size={18} />}
          className="mt-1 text-sm font-semibold"
          onPress={clearAlerts}
        >
          CLEAR ALL
        </Button>
      </CardBody>
    </Card>
  );
}