import React, { useState } from "react";
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
} from "@heroui/react";
import { Trash2, ChevronRight } from "lucide-react";

const shipTypes = [
  "Cargo Vessels",
  "Tankers",
  "Passenger Ships",
  "Fishing Ships",
  "High Speed Crafts",
  "Pleasure Crafts",
  "Unspecified",
];

export default function FiltersMenu({onFiltersChange, onClearFilters}) {
  const [selectedShipTypes, setSelectedShipTypes] = useState([]);

  const toggleShipType = (type) => {
    const updated = selectedShipTypes.includes(type)
      ? selectedShipTypes.filter((t) => t !== type)
      : [...selectedShipTypes, type];

    setSelectedShipTypes(updated);
    onFiltersChange?.(updated.length > 0);
  };

  const clearFilters = () => {
    setSelectedShipTypes([]);
    onClearFilters?.(); // Notify parent component to clear filters
    onFiltersChange?.(false); // Notify parent that no filters are active
    // clear other filters if added
  };

  return (
    <Card
      isBlurred
      className={`
      fixed right-4 top-1/2 -translate-y-1/2 z-[1100]
      transition-all duration-600 ease-in-out overflow-hidden
      w-[160px] bg-white/90 dark:bg-black/90
      shadow-xl border-none
    `}
    >
      <CardHeader className="text-lg font-bold px-4 pt-4 pb-2 flex items-center justify-center">
        Filters
      </CardHeader>
      <CardBody className="p-4 pt-2 flex flex-col gap-4">
        <Divider />
        {/* Filter From */}
        <div>
          <label className="text-sm font-medium block mb-1">FILTER FROM</label>
          <Select defaultSelectedKeys={["all"]} size="sm">
            <SelectItem key="all" value="all">
              All
            </SelectItem>
            <SelectItem key="myfleet" value="myfleet">
              My Fleet
            </SelectItem>
          </Select>
        </div>

        <Divider />

        {/* Ship Type */}
        <Accordion isCompact>
          <AccordionItem key="ship-type" aria-label="SHIP TYPE" title="SHIP TYPE">
            <div className="flex flex-col gap-2 pt-2">
              {shipTypes.map((type) => (
                <Checkbox
                  key={type}
                  isSelected={selectedShipTypes.includes(type)}
                  onValueChange={() => toggleShipType(type)}
                  size="sm"
                  color='default'
                >
                  {type}
                </Checkbox>
              ))}
            </div>
          </AccordionItem>

          {/* Placeholder AccordionItems for other filter categories */}
          <AccordionItem key="status" aria-label="CURRENT STATUS" title="CURRENT STATUS">
            <p className="text-sm text-default-500">Status filters here...</p>
          </AccordionItem>
          <AccordionItem key="other" aria-label="OTHER" title="OTHER">
            <p className="text-sm text-default-500">Other filters here...</p>
          </AccordionItem>
        </Accordion>

        <Divider />

        {/* Clear All Button */}
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
