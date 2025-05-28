import React, { useState, useEffect } from 'react';
import { Button } from '@heroui/react';
import { Trash2, Clock, Plus } from 'lucide-react';
import { getColorByStatus } from '../utils/statusColor';

export default function VesselInfo({ ship }) {
	const [inFleet, setInFleet] = useState(false);


  if (!ship) return null;

	const statusColor = getColorByStatus(ship.status);

	useEffect(() => {
    const fetchFleet = async () => {
      try {
        const token = localStorage.getItem('token');
        const res = await fetch('https://localhost:8443/registered-user/get-my-fleet', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
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
    const token = localStorage.getItem('token');
    const url = inFleet
      ? 'https://localhost:8443/registered-user/remove-vessel-from-fleet'
      : 'https://localhost:8443/registered-user/add-vessel-to-fleet';

    try {
      const res = await fetch(url, {
        method: 'PUT',
        headers: {
          'Content-Type': 'text/plain',
          Authorization: `Bearer ${token}`,
        },
        body: ship.mmsi,
      });

      if (!res.ok) throw new Error(`Failed to ${inFleet ? 'remove' : 'add'} vessel`);

      setInFleet(!inFleet);
    } catch (err) {
      console.error(err.message);
      // You could show a toast here if you want
    }
  };

  return (
    <div className="flex flex-col items-center bg-white dark:bg-zinc-900 p-4 rounded-xl shadow-md text-sm w-[350px]">
      {/* MMSI and Vessel Type */}
      <h3 className="text-base font-semibold text-gray-900 dark:text-gray-100">
        MMSI: {ship.mmsi}
      </h3>
      <p className="text-xs text-gray-500 dark:text-gray-400">
        {ship.vesselType || 'Unknown Type'}
      </p>

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
        <Button size="sm" color="default" variant="ghost" className="w-full">
          <Clock className="w-4 h-4 mr-1 inline-block" />
          Show past track
        </Button>
      </div>

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
