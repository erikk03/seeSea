import React, { useEffect, useState } from "react";
import {
  Card,
  CardHeader,
  CardBody,
  ScrollShadow,
  Input,
  Listbox,
  ListboxItem,
  Chip,
  Button,
} from "@heroui/react";
import { Trash2 } from "lucide-react";

export default function MyVessels({ onLoadFleet }) {
  const [fleet, setFleet] = useState([]);
  const [search, setSearch] = useState("");

  const fetchFleetAndUpdateMap = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch("https://localhost:8443/registered-user/get-my-fleet", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

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
  }, []);

  const removeVessel = async (mmsi) => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch("https://localhost:8443/registered-user/remove-vessel-from-fleet", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ mmsi }),
      });

      if (!res.ok) throw new Error("Failed to remove vessel");

      const updated = fleet.filter(v => v.mmsi !== mmsi);
      setFleet(updated);

      // Update map based on remaining vessels (still filterFrom = MyFleet)
      onLoadFleet?.({
        filterFrom: "MyFleet",
        vesselStatusIds: [],
        vesselTypeIds: [],
      });
    } catch (err) {
      console.error("Remove failed:", err);
    }
  };

  const getColorByStatus = (status) => {
		if (!status) return "#A1A1AA"; // default gray

		const normalized = status.toLowerCase();

		switch (normalized) {
			case "under way using engine":
			case "under way sailing":
			case "engaged in fishing":
				return "#22c55e"; // green-500

			case "moored":
			case "at anchor":
				return "#facc15"; // yellow-400

			case "not under command":
			case "restricted manoeuvrability":
			case "constrained by her draught":
			case "aground":
				return "#ef4444"; // red-500 (warning or danger)

			case "not defined = default (also used by ais-sart under test)":
			case "ais-sart (active)":
			case "reserved for future amendment of navigational status for ships carrying dg":
			case "reserved for future amendment of navigational status for ships carrying dangerous goods (dg)":
			case "reserved for future use":
				return "#a1a1aa"; // gray-400

			default:
				return "#a1a1aa"; // fallback to gray
		}
	};



  const filteredFleet = fleet.filter(v =>
    v.mmsi.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <Card
      isBlurred
      className="fixed right-4 top-1/2 -translate-y-1/2 z-[1100]
      w-[220px] bg-white/90 dark:bg-black/90 shadow-xl border-none"
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
