import React, { useEffect, useState } from "react";
import {
  Card,
  CardHeader,
  CardBody,
  Divider,
  Checkbox,
  Accordion,
  AccordionItem,
  Select,
  SelectItem,
  Button,
  ScrollShadow,
} from "@heroui/react";
import { Trash2 } from "lucide-react";

export default function FiltersMenu({ onFiltersChange, onClearFilters }) {
  const [availableFilters, setAvailableFilters] = useState(null);
  const [selectedVesselTypeIds, setSelectedVesselTypeIds] = useState([]);
  const [selectedStatusIds, setSelectedStatusIds] = useState([]);
  const [filterFrom, setFilterFrom] = useState("all");

  useEffect(() => {
    const fetchFilters = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await fetch("https://localhost:8443/filters/get-available-filters", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error(`HTTP ${res.status} - ${res.statusText}`);
        const data = await res.json();
        setAvailableFilters(data);
      } catch (err) {
        console.error("Failed to fetch filters", err);
      }
    };

    fetchFilters();
  }, []);

  const toggleVesselType = (id) => {
    const updated = selectedVesselTypeIds.includes(id)
      ? selectedVesselTypeIds.filter((i) => i !== id)
      : [...selectedVesselTypeIds, id];

    setSelectedVesselTypeIds(updated);
    onFiltersChange?.(updated.length > 0 || selectedStatusIds.length > 0);
  };

  const toggleStatus = (id) => {
    const updated = selectedStatusIds.includes(id)
      ? selectedStatusIds.filter((i) => i !== id)
      : [...selectedStatusIds, id];

    setSelectedStatusIds(updated);
    onFiltersChange?.(selectedVesselTypeIds.length > 0 || updated.length > 0);
  };

  const clearFilters = () => {
    setSelectedVesselTypeIds([]);
    setSelectedStatusIds([]);
    setFilterFrom("all");
    onClearFilters?.();
    onFiltersChange?.(false);
  };

  return (
    <Card
      isBlurred
      className="fixed right-4 top-1/2 -translate-y-1/2 z-[1100]
      transition-all duration-600 ease-in-out overflow-hidden
      w-[200px] bg-white/90 dark:bg-black/90
      shadow-xl border-none"
    >
      <CardHeader className="text-lg font-bold px-4 pt-4 flex items-center justify-center">
        Filters
      </CardHeader>

      <CardBody className="p-4 flex flex-col gap-4">
        <Divider />

        {/* Filter From */}
        <div>
          <label className="text-sm font-medium block mb-1">FILTER FROM</label>
          <Select
            isRequired
            size="sm"
            aria-label="Filter from"
            selectedKeys={[filterFrom]}
            onSelectionChange={(keys) => setFilterFrom([...keys][0])}
          >
            <SelectItem key="all" value="all">All</SelectItem>
            <SelectItem key="myfleet" value="myfleet">My Fleet</SelectItem>
          </Select>
        </div>

        <Divider />

        <Accordion isCompact>
          {/* Vessel Types */}
          <AccordionItem key="vessel-types" aria-label="Vessel Types" title="SHIP TYPE">
            <ScrollShadow className="max-h-[250px]">
              <div className="flex flex-col gap-2 pt-2">
                {availableFilters?.vesselTypes?.map((type) => (
                  <Checkbox
                    key={type.id}
                    isSelected={selectedVesselTypeIds.includes(type.id)}
                    onValueChange={() => toggleVesselType(type.id)}
                    size="sm"
                    color="default"
                  >
                    {type.name}
                  </Checkbox>
                ))}
              </div>
            </ScrollShadow>
          </AccordionItem>


          {/* Vessel Statuses */}
          <AccordionItem key="status" aria-label="Status" title="CURRENT STATUS">
            <ScrollShadow className="max-h-[250px]">
              <div className="flex flex-col gap-2 pt-2">
                {availableFilters?.vesselStatuses?.map((status) => (
                  <Checkbox
                    key={status.id}
                    isSelected={selectedStatusIds.includes(status.id)}
                    onValueChange={() => toggleStatus(status.id)}
                    size="sm"
                    color="default"
                  >
                    {status.name}
                  </Checkbox>
                ))}
              </div>
            </ScrollShadow>
          </AccordionItem>

          {/* Placeholder for other filters */}
          <AccordionItem key="other" aria-label="Other" title="OTHER">
            <p className="text-sm text-default-500">Other filters here...</p>
          </AccordionItem>
        </Accordion>

        <Divider />

        <Button
          variant="light"
          color="danger"
          startContent={<Trash2 size={18} />}
          className="mt-1 text-sm font-semibold"
          onPress={clearFilters}
        >
          CLEAR ALL
        </Button>
      </CardBody>
    </Card>
  );
}
