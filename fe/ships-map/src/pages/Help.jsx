import React from "react";
import {
  Card,
  CardHeader,
  CardBody,
  Accordion,
  AccordionItem,
  Divider,
  ScrollShadow,
  Button,
} from "@heroui/react";
import {useNavigate} from "react-router-dom";
import { ArrowLeft } from "lucide-react";

export default function Help() {
  const navigate = useNavigate();

  const onLeftArrowClick = () => {
    const token = localStorage.getItem("token");
    if (token) {
      navigate("/registered-map");
    } else {
      navigate("/guest-map");
    }
  }

  return (
    <div className="relative min-h-screen bg-neutral-200 text-black dark:bg-gray-800 dark:text-white flex justify-center items-start p-6">
      <Button
        isIconOnly
        variant="light"
        onClick={onLeftArrowClick}
        className="absolute top-4 left-4 z-10"
        aria-label="Back to map"
      >
        <ArrowLeft />
      </Button>

      <Card className="w-full max-w-2xl">
        <CardHeader className="text-2xl font-bold text-center py-4 flex items-center justify-center">
          Help & Frequently Asked Questions
        </CardHeader>

        <Divider className="my-6"/>

        <CardBody className="p-6 h-[700px]">
          <ScrollShadow className="h-full p-3">
            <Accordion variant="splitted" className="text-black dark:text-white">
              <AccordionItem key="1" title="How do I sign in?">
                Click the "Sign In" button at the top right, then enter your email and password.
                If you don't have an account, you can click "Sign Up" instead.
              </AccordionItem>

              <AccordionItem key="2" title="What is 'My Fleet'?">
                "My Fleet" shows only the vessels you've added to your fleet. You must be signed in to use this feature.
              </AccordionItem>

              <AccordionItem key="3" title="How do I apply filters to the map?">
                Open the side menu, click "Filters", and use the filter options like vessel type or status to narrow down the map results.
              </AccordionItem>

              <AccordionItem key="4" title="Why can't I access certain features?">
                Some features require a registered account. If you're using the app as a guest, you’ll be prompted to sign in to access them.
              </AccordionItem>

              <AccordionItem key="5" title="How do I reset filters?">
                In the filter panel, click the "Clear All" button. This will remove all applied filters and reset the map.
              </AccordionItem>

              <AccordionItem key="6" title="Can I combine multiple filters?">
                Yes! You can filter by vessel type, status, and source (like 'My Fleet') all at once to narrow down the map results more precisely.
              </AccordionItem>

              <AccordionItem key="7" title="Why does the map show no vessels after applying filters?">
                It's possible no vessels match all selected filters. Try simplifying or clearing the filters to refresh results.
              </AccordionItem>

              <AccordionItem key="8" title="How do I add a vessel to My Fleet?">
                Click on a vessel on the map, then in the popup, click “Add to fleet”. You must be signed in to use this feature.
              </AccordionItem>

              <AccordionItem key="9" title="How do I remove a vessel from My Fleet?">
                You can remove a vessel either from the popup on the map or from the “My Fleet” side menu by clicking the trash icon next to the vessel.
              </AccordionItem>

              <AccordionItem key="11" title="What are alerts and when do they appear?">
                Alerts notify you about important events, like unusual vessel activity or rule violations. They appear in real time for signed-in users.
              </AccordionItem>

              <AccordionItem key="12" title="Can I disable alerts?">
                Alerts are applied by the user based on the zone of interest selected. They can be turned on and off.
              </AccordionItem>

              <AccordionItem key="13" title="How do I change my password?">
                Go to your Profile by clicking the user icon in the top right, then choose "Change Password" and follow the instructions.
              </AccordionItem>

              <AccordionItem key="14" title="Can I update my email or account info?">
                Email and basic profile details will be editable from the Profile page in a future update.
              </AccordionItem>
            </Accordion>
          </ScrollShadow>

          <Divider className="my-6" />

          <div className="text-sm text-center">
            Need more help? Contact us at{" "}
            <a
              href="mailto:support@example.com"
              className="text-blue-600 hover:underline"
            >
              support@seesea.com
            </a>
          </div>
        </CardBody>
      </Card>
    </div>
  );
}
