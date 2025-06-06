import React, { useState, useEffect } from 'react';
import { Button, addToast, Select, SelectItem } from '@heroui/react';
import { Trash2, Clock, Plus, Pencil } from 'lucide-react';
import { getColorByStatus } from '../utils/statusColor';
import { authFetch } from '../utils/authFetch';

export default function VesselInfo({ ship, onShowTrack, onFleetChanged }) {
	const [inFleet, setInFleet] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [selectedVesselType, setSelectedVesselType] = useState(ship.vesselType || 'unknown');
  const [vesselTypes, setVesselTypes] = useState([]);

  if (!ship) return null;

	const statusColor = getColorByStatus(ship.status);
	const token = localStorage.getItem('token');
  // Check if user is admin
  useEffect(() => {
    if (!token) return;

    const fetchUserInfo = async () => {
      try {
        const res = await authFetch('https://localhost:8443/registered-user/get-user-info');
        if (!res.ok) throw new Error('Failed to fetch user info');
        const data = await res.json();
        setIsAdmin(data.role === 'Administrator');
      } catch (err) {
        console.error('Error fetching user info:', err);
      }
    };

    fetchUserInfo();
  }, []);

  // Fetch vessel types for admin users
  useEffect(() => {
    if (!isAdmin) return;

    const fetchVesselTypes = async () => {
      try {
        const res = await authFetch('https://localhost:8443/admin/get-vessel-types');
        if (!res.ok) throw new Error('Failed to fetch vessel types');
        const data = await res.json();
        setVesselTypes(data);

        const match = data.find(v => v.name === ship.vesselType);
        setSelectedVesselType(match?.name || data[0]?.name); // fallback to first option
      } catch (err) {
        console.error('Error fetching vessel types:', err);
      }
    };

    fetchVesselTypes();
  }, [isAdmin]);

	useEffect(() => {
    if (!token) return; // For guest users, skip fleet check 

    const fetchFleet = async () => {
      try {
        const res = await authFetch('https://localhost:8443/registered-user/get-my-fleet', {
        });
        if (!res.ok) throw new Error('Failed to fetch fleet');
        const data = await res.json();
        const isInFleet = data.myFleet.some(v => v.mmsi === ship.mmsi);
        setInFleet(isInFleet);
      } catch (err) {
        console.error('Error fetching fleet:', err);
      }
    };

    fetchFleet();
  }, [ship.mmsi]);

	const handleFleetToggle = async () => {
    if (!token) return; // For guest users, do nothing

    const url = inFleet
      ? 'https://localhost:8443/registered-user/remove-vessel-from-fleet'
      : 'https://localhost:8443/registered-user/add-vessel-to-fleet';

    try {
      const res = await authFetch(url, {
        method: 'PUT',
        headers: {
          'Content-Type': 'text/plain',
        },
        body: ship.mmsi,
      });

			// Show success toast
			addToast({
				title: `Vessel ${ship.mmsi} ${inFleet ? 'removed' : 'added'}`,
				description: `Successfully ${inFleet ? 'removed from' : 'added to'} fleet.`,
				timeout: 3000,
				shouldShowTimeoutProgress: true,
				variant: "bordered",
			});

      if (!res.ok) throw new Error(`Failed to ${inFleet ? 'remove' : 'add'} vessel`);

      setInFleet(!inFleet);
      onFleetChanged?.();
    } catch (err) {
      console.error(err.message);
      // Show error toast
			addToast({
				title: `Error`,
				description: `Failed to ${inFleet ? 'remove' : 'add'} vessel: ${err.message}`,
				variant: 'solid',
				color: 'danger',
				timeout: 5000,
				shouldShowTimeoutProgress: true,
			});
    }
  };

  const updateVesselType = async (newType) => {
    try {
      const res = await authFetch('https://localhost:8443/admin/change-vessel-type', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          mmsi: ship.mmsi,
          newType,
        }),
      });

      if (!res.ok) throw new Error('Failed to update vessel type');

      setSelectedVesselType(newType);

      addToast({
        title: 'Vessel type updated',
        description: `New type: ${newType}`,
        timeout: 3000,
        shouldShowTimeoutProgress: true,
        variant: 'bordered',
      });

    } catch (err) {
      console.error(err);
      addToast({
        title: 'Error',
        description: `Failed to update vessel type: ${err.message}`,
        timeout: 5000,
        color: 'danger',
        variant: 'solid',
        shouldShowTimeoutProgress: true,
      });
    }
  };

  return (
    <div className="flex flex-col items-center bg-white/70 dark:bg-black/30 p-4 rounded-xl shadow-md text-sm w-[350px]">
      {/* MMSI and Vessel Type */}
      <h3 className="text-base font-semibold text-gray-900 dark:text-gray-100">
        MMSI: {ship.mmsi}
      </h3>
      {isAdmin ? (
        vesselTypes.length > 0 && vesselTypes.some(t => t.name === selectedVesselType) ? (
          <div className="w-full my-2">
            <Select
              label="Vessel Type:"
              className="max-w-xs"
              selectedKeys={new Set([selectedVesselType])}
              onSelectionChange={(keys) => {
                const [key] = Array.from(keys);
                updateVesselType(key);
              }}
              startContent={<Pencil size={16}/>}
              labelPlacement='outside-left'
              variant='bordered'
            >
              {vesselTypes.map((type) => (
                <SelectItem key={type.name}>{type.name}</SelectItem>
              ))}
            </Select>
          </div>
        ) : (
          <p className="text-xs text-gray-500 dark:text-gray-400 italic">Loading vessel types...</p>
        )
      ) : (
        <p className="text-xs text-gray-500 dark:text-gray-400">
          {ship.vesselType || 'Unknown Type'}
        </p>
      )}


      {/* Custom badge-style status */}
      {ship.status && (
        <span
          className="text-xs px-2 py-1 rounded-full font-semibold mb-2"
          style={{
            backgroundColor: `${statusColor}33`, // apply transparent BG (20%)
            color: statusColor,
          }}
        >
          {ship.status}
        </span>
      )}

      {/* Speed */}
      <p className="text-xs text-gray-700 dark:text-gray-300">
        Speed: <span className="font-bold text-lg">{ship.speed?.toFixed(1)} kn</span>
      </p>

      {/* Action Buttons */}
			{ token && (
				<div className="flex gap-2 w-full">
					<Button
						size="sm"
						variant="ghost"
						color={inFleet ? 'danger' : 'success'}
						onClick={handleFleetToggle}
						className="w-full"
					>
						{inFleet ? (
							<>
								<Trash2 className="w-4 h-4 mr-1 inline-block" />
								Remove from fleet
							</>
						) : (
							<>
								<Plus className="w-4 h-4 mr-1 inline-block" />
								Add to fleet
							</>
						)}
					</Button>
					<Button size="sm" color="default" variant="ghost" className="w-full" onClick={() => onShowTrack?.(ship.mmsi)} >
						<Clock className="w-4 h-4 mr-1 inline-block" />
						Show past track
					</Button>
				</div>
			)}

      {/* Timestamp */}
      <p className="text-xs text-gray-500 dark:text-gray-400 mt-2">
        {ship.timestamp
          ? new Date(ship.timestamp * 1000).toLocaleString()
          : 'No timestamp'}
      </p>

      {/* Bottom green bar */}
      <div className="w-2/3 h-1 bg-green-500 rounded-lg" style={{ backgroundColor: statusColor }}/>
    </div>
  );
}
