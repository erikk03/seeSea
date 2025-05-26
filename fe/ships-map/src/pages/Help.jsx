import React from "react";
import {
  Card,
  CardHeader,
  CardBody,
  Accordion,
  AccordionItem,
  Divider,
} from "@heroui/react";

export default function Help() {
  return (
    <div className="min-h-screen bg-white text-black dark:bg-black dark:text-white flex justify-center items-start p-6">
      <Card className="w-full max-w-2xl">
        <CardHeader className="text-2xl font-bold text-center py-4 flex items-center justify-center">
          Help & Frequently Asked Questions
        </CardHeader>

        <Divider />

        <CardBody className="p-6">
          <Accordion variant="splitted">
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
          </Accordion>

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
