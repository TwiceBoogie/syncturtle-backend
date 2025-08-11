import { FC } from "react";
import Link from "next/link";
import Image from "next/image";

import PlaneBackgroundPatternDark from "@/public/auth/background-pattern-dark.svg";
import WhiteHorizontalLogo from "@/public/syncturtle-logos/white-horizontal-logo-with-text.png";
import PlaneTakeOffImage from "@/public/plane-takeoff.png";
import { GOD_MODE_URL } from "@/helpers/common.helper";
import { Button } from "@heroui/button";

export const InstanceNotReady: FC = () => {
  return (
    <div className="relative">
      <div className="h-screen w-full overflow-hidden overflow-y-auto flex flex-col">
        <div className="container h-[110px] flex-shrink-0 mx-auto px-5 lg:px-0 flex items-center justify-between gap-5 z-50">
          <div className="flex items-center gap-x-2 py-10">
            <Link href={`/`} className="h-[30px] w-[133px]">
              <Image src={WhiteHorizontalLogo} alt="SyncTurtle logo" />
            </Link>
          </div>
        </div>

        <div className="absolute inset-0 z-0">
          <Image src={PlaneBackgroundPatternDark} className="w-screen h-full object-cover" alt="background pattern" />
        </div>

        <div className="relative z-10 mb-[110px] flex-grow">
          <div className="h-full w-full relative container px-5 mx-auto flex justify-center items-center">
            <div className="w-auto max-w-2xl relative space-y-8 py-10">
              <div className="relative flex flex-col justify-center items-center space-y-4">
                <h1 className="text-3xl font-bold pb-3">Welcome aboard Plane!</h1>
                <Image src={PlaneTakeOffImage} alt="Plane Logo" />
                <p className="font-medium text-base text-onboarding-text-400">
                  Get started by setting up your instance and workspace
                </p>
              </div>
              <div>
                <a href={GOD_MODE_URL}>
                  <Button size="lg" color="primary" className="w-full">
                    Get started
                  </Button>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
