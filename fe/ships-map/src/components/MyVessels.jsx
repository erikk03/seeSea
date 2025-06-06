import React, { useEffect, useState } from "react";
import {
  addToast,
  Card,
  CardHeader,
  CardBody,
  ScrollShadow,
  Input,
  Listbox,
  ListboxItem,
  Button,
} from "@heroui/react";
import { Trash2 } from "lucide-react";
import { getColorByStatus } from "../utils/statusColor";
import { authFetch } from "../utils/authFetch";

export default function MyVessels({ onLoadFleet, fleetRefreshToggle }) {
  const [fleet, setFleet] = useState([]);
  const [search, setSearch] = useState("");

  const fetchFleetAndUpdateMap = async () => {
    try {
      const res = await authFetch("https://localhost:8443/registered-user/get-my-fleet");

      if (!res.ok) throw new Error("Failed to load fleet");
      const data = await res.json();
      setFleet(data.myFleet);

      // Send filters to parent for map update
      onLoadFleet?.({
        filterFrom: "MyFleet",
        vesselStatusIds: [],
        vesselTypeIds: [],
      });
    } catch (err) {
      console.error("Error fetching fleet:", err);
    }
  };

  useEffect(() => {
    fetchFleetAndUpdateMap();
  }, [fleetRefreshToggle]);


  const removeVessel = async (mmsi) => {
    try {
      const res = await authFetch("https://localhost:8443/registered-user/remove-vessel-from-fleet", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: mmsi,
      });

      if (!res.ok) throw new Error("Failed to remove vessel");

      const updated = fleet.filter(v => v.mmsi !== mmsi);
      setFleet(updated);

			// Show success toast
			addToast({
        title: `Vessel ${mmsi} removed`,
        description: "Successfully removed from fleet.",
				timeout: 3000,
				shouldShowTimeoutProgress: true,
				variant: "bordered",
      });

      // Update map based on remaining vessels (still filterFrom = MyFleet)
      onLoadFleet?.({
        filterFrom: "MyFleet",
        vesselStatusIds: [],
        vesselTypeIds: [],
      });
    } catch (err) {
      console.error("Remove failed:", err);

			// Show error toast
			addToast({
        title: "Failed to remove vessel",
        description: err.message,
        timeout: 3000,
				shouldShowTimeoutProgress: true,
				color: "danger",
				variant: "bordered",
      });
    }
  };

  const filteredFleet = fleet.filter(v =>
    v.mmsi.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <Card
      isBlurred
      className="fixed right-4 top-1/2 -translate-y-1/2 z-[1100]
      transition-all duration-600 ease-in-out overflow-hidden
      w-[220px] bg-neutral-100/50 dark:bg-neutral-900/50 shadow-xl border-none"
    >
      <CardHeader className="text-lg font-bold px-4 pt-4 pb-2 text-center flex items-center justify-center">
        My Vessels
      </CardHeader>

      <CardBody className="p-2 pt-2 flex flex-col gap-4 h-[360px]">
        <Input
          placeholder="Search Vessel MMSI"
          radius='sm'
          value={search}
          onValueChange={setSearch}
          size="sm"
          isClearable
          aria-label="Search vessel"
        />

        <ScrollShadow className="max-h-[400px]">
          <Listbox
            aria-label="My Fleet"
            variant="flat"
            className="text-sm text-black dark:text-white"
          >
            {filteredFleet.map(vessel => (
              <ListboxItem
                key={vessel.mmsi}
                textValue={vessel.mmsi}
                endContent={
                  <Button
                    isIconOnly
                    variant="light"
                    size="sm"
                    color="danger"
                    onClick={() => removeVessel(vessel.mmsi)}
                  >
                    <Trash2 size={12} />
                  </Button>
                }
              >
                <div className="flex items-center gap-2">
                  <div className="w-[3px] h-7 rounded-full" style={{ backgroundColor: getColorByStatus(vessel.status) }}></div>
                  <div className="flex flex-col">
                    <span className="font-medium">MMSI: {vessel.mmsi}</span>
                    <span className="text-xs text-default-500">{vessel.type}</span>
                  </div>
                </div>
              </ListboxItem>
            ))}
          </Listbox>
        </ScrollShadow>
      </CardBody>
    </Card>
  );
}
