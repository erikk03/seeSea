import React, { useEffect, useState } from "react";
import { authFetch } from "../utils/authFetch"; 
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

export default function FiltersMenu({ selectedFilters, onFiltersChange, onClearFilters }) {
  const [availableFilters, setAvailableFilters] = useState(null);
  const [selectedVesselTypeIds, setSelectedVesselTypeIds] = useState([]);
  const [selectedStatusIds, setSelectedStatusIds] = useState([]);
  const [filterFrom, setFilterFrom] = useState("All");

  useEffect(() => {
    const fetchFilters = async () => {
      try {
        const [filtersRes, currentFiltersRes] = await Promise.all([
          authFetch("https://localhost:8443/filters/get-available-filters"),
          authFetch("https://localhost:8443/filters/get-current-filters"),
        ]);

        if (!filtersRes.ok || !currentFiltersRes.ok)
          throw new Error("Failed to fetch filters");

        const available = await filtersRes.json();
        const current = await currentFiltersRes.json();

        setAvailableFilters(available);
        setSelectedVesselTypeIds(current.vesselTypeIds || []);
        setSelectedStatusIds(current.vesselStatusIds || []);
        setFilterFrom(current.filterFrom || "All");

        // Notify parent on initial load
        onFiltersChange?.({
          filterFrom: current.filterFrom || "All",
          vesselTypeIds: current.vesselTypeIds || [],
          vesselStatusIds: current.vesselStatusIds || [],
        });
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
    onFiltersChange?.({
      filterFrom,
      vesselTypeIds: updated,
      vesselStatusIds: selectedStatusIds,
    });
  };

  const toggleStatus = (id) => {
    const updated = selectedStatusIds.includes(id)
      ? selectedStatusIds.filter((i) => i !== id)
      : [...selectedStatusIds, id];

    setSelectedStatusIds(updated);
    onFiltersChange?.({
      filterFrom,
      vesselTypeIds: selectedVesselTypeIds,
      vesselStatusIds: updated,
    });

  };

  const handleFilterFromChange = (keys) => {
    const selected = [...keys][0];
    setFilterFrom(selected);
    onFiltersChange?.({
      filterFrom: selected,
      vesselTypeIds: selectedVesselTypeIds,
      vesselStatusIds: selectedStatusIds,
    });
  };

  const clearFilters = () => {
    setSelectedVesselTypeIds([]);
    setSelectedStatusIds([]);
    setFilterFrom("All");
    onClearFilters?.();
    onFiltersChange?.({
      filterFrom: "All",
      vesselTypeIds: [],
      vesselStatusIds: [],
    });
  };

  // listen for changes in selectedFilters prop
  useEffect(() => {
    setSelectedVesselTypeIds(selectedFilters?.vesselTypeIds || []);
    setSelectedStatusIds(selectedFilters?.vesselStatusIds || []);
    setFilterFrom(selectedFilters?.filterFrom || "All");
  }, [selectedFilters]);

  return (
    <Card
      isBlurred
      className="fixed right-4 top-1/2 -translate-y-1/2 z-[1100]
      transition-all duration-600 ease-in-out overflow-hidden
      w-[200px] bg-neutral-100/50 dark:bg-neutral-900/50 shadow-xl border-none"
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
            onSelectionChange={handleFilterFromChange}
          >
            <SelectItem key="All" value="All">All</SelectItem>
            <SelectItem key="MyFleet" value="MyFleet">My Fleet</SelectItem>
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
