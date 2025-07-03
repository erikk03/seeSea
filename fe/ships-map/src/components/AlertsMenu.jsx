import React, { useState, useEffect } from "react";
import { authFetch } from "../utils/authFetch";
import {
  Card,
  CardHeader,
  CardBody,
  Divider,
  NumberInput,
  Switch,
  Button,
} from "@heroui/react";
import { Trash2 } from "lucide-react";

export default function AlertsMenu({ alerts, onAlertsChange, onStartZoneSelection, onRemoveZone, zone, zoneDrawing, onCancelZoneDrawing, onClearAlerts}) {
  const [speedThreshold, setSpeedThreshold] = useState(alerts?.speedThreshold || null);
  const [enterZoneEnabled, setEnterZoneEnabled] = useState(alerts?.enterZoneEnabled || false);
  const [exitZoneEnabled, setExitZoneEnabled] = useState(alerts?.exitZoneEnabled || false);
  const [collisionsEnabled, setCollisionsEnabled] = useState(alerts?.collisionsEnabled || false);


  // Fetch current alerts from the backend when the component loads
  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const res = await authFetch("https://localhost:8443/zone-of-interest/get-zone-options", {
          headers: {
            "Content-Type": "application/json"
          }
        });
        if (!res.ok) throw new Error("Failed to fetch alert options");
        const data = await res.json();

        // Update local state with fetched data
        setSpeedThreshold(data.maxSpeed ?? null);
        setEnterZoneEnabled(data.entersZone ?? false);
        setExitZoneEnabled(data.exitsZone ?? false);
        setCollisionsEnabled(data.collisionMonitoring ?? false);

        // Notify parent about the fetched data
        onAlertsChange?.({
          speedThreshold: data.maxSpeed ?? null,
          enterZoneEnabled: data.entersZone ?? false,
          exitZoneEnabled: data.exitsZone ?? false,
          collisionsEnabled: data.collisionMonitoring ?? false
        });
      } catch (err) {
        console.error("Error fetching alert options:", err);
    }
    };

    fetchAlerts();
  }, []); // run only on load

   useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'c' || e.key === 'C') {
        if (zoneDrawing) {
          onCancelZoneDrawing?.();
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [zoneDrawing, onCancelZoneDrawing]);

  useEffect(() => {
    setSpeedThreshold(alerts?.speedThreshold || null);
    setEnterZoneEnabled(alerts?.enterZoneEnabled || false);
    setExitZoneEnabled(alerts?.exitZoneEnabled || false);
    setCollisionsEnabled(alerts?.collisionsEnabled || false);
  }, [alerts]);


  const handleSpeedChange = (value) => {
    const parsedValue = value === "" || value === undefined || isNaN(value) ? null : value;
    // Manual validation: allow null, but block negatives
    if (parsedValue !== null && parsedValue < 0) {
      return; // Ignore invalid input
    }

    setSpeedThreshold(parsedValue);
    onAlertsChange?.({ speedThreshold: parsedValue, enterZoneEnabled, exitZoneEnabled, collisionsEnabled });
  };

  const handleEnterToggle = (val) => {
    setEnterZoneEnabled(val);
    onAlertsChange?.({ speedThreshold, enterZoneEnabled: val, exitZoneEnabled, collisionsEnabled });
  };

  const handleExitToggle = (val) => {
    setExitZoneEnabled(val);
    onAlertsChange?.({ speedThreshold, enterZoneEnabled, exitZoneEnabled: val, collisionsEnabled });
  };

  const handleCollisionsToggle = (val) => {
    setCollisionsEnabled(val);
    onAlertsChange?.({ speedThreshold, enterZoneEnabled, exitZoneEnabled, collisionsEnabled: val });
  }

  const clearAlerts = () => {
    setSpeedThreshold(null);
    setEnterZoneEnabled(false);
    setExitZoneEnabled(false);
    setCollisionsEnabled(false);
    onClearAlerts?.(); // Notify parent to clear alerts
    onAlertsChange?.({ speedThreshold: null, enterZoneEnabled: false, exitZoneEnabled: false });
  };

  return (
    <Card
      isBlurred
      className="fixed right-4 top-1/2 -translate-y-1/2 z-[1100]
      transition-all duration-600 ease-in-out overflow-hidden
      w-[240px] bg-neutral-100/50 dark:bg-neutral-900/50
      shadow-xl border-none"
    >
      <CardHeader className="text-lg font-bold px-4 pt-4 flex items-center justify-center">
        Alerts
      </CardHeader>

      <CardBody className="p-4 flex flex-col gap-4">
          <Divider />
        
        <div className="flex gap-2">
          {zoneDrawing ? (
            <Button
              size="sm"
              variant="solid"
              color='warning'
              className="w-full text-sm font-semibold"
              onPress={onCancelZoneDrawing}
            >
              Cancel [ or Press 'C' ]
            </Button>
          ):(
            <Button
            size="sm"
            variant="solid"
            color={zone ? "danger" : "primary"}
            className="w-full text-sm font-semibold"
            onPress={zone ? onRemoveZone : onStartZoneSelection}
            >
              {zone ? "Remove Zone of Interest" : "Set Zone of Interest"}
            </Button>
          )}
        </div>

        <Divider />


        <div className="flex flex-col gap-2">
          <NumberInput
            minValue={0.1}
            step={0.1}
            size="sm"
            label="Speed Threshold (knots)"
            placeholder="Enter speed threshold"
            labelPlacement="outside"
            value={speedThreshold}
            onValueChange={handleSpeedChange}
          />
        </div>

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
          
          <div className="flex justify-between items-center">
            <span className="text-sm font-medium">Collisions</span>
            <Switch
              size="sm"
              isSelected={collisionsEnabled}
              onValueChange={handleCollisionsToggle}
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