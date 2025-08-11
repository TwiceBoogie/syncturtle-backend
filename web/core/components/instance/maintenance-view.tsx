import { FC } from "react";
import Image from "next/image";

import { Button } from "@heroui/button";

import DefaultLayout from "@/layouts/default-layout";
import maintenanceModeImage from "@/public/maintenance-mode.webp";
import { MaintenanceMessage } from "./maintenance-message";

export const MaintenanceView: FC = () => {
  return (
    <DefaultLayout>
      <div className="relative container mx-auto h-full w-full flex flex-col md:flex-row gap-2 items-center justify-center gap-y-5 bg-custom-background-100 text-center">
        <div className="relative w-full">
          <Image
            src={maintenanceModeImage}
            height="176"
            width="288"
            alt="ProjectSettingImg"
            className="w-full h-full object-fill object-center"
            priority={true}
          />
        </div>
        <div className="w-full space-y-4 flex flex-col justify-center md:justify-start items-center md:items-start">
          <MaintenanceMessage />
          <Button onPress={() => window.location.reload()}>Reload</Button>
        </div>
      </div>
    </DefaultLayout>
  );
};
