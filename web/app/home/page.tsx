"use client";

import { AppHeader } from "@/components/core";
import { WorkspaceDashboardHeader } from "./header";
import { ContentWrapper } from "@/components/core/content-wrapper";
import { Shapes } from "lucide-react";
import { number } from "framer-motion";
import { Avatar } from "@heroui/avatar";
import { Card, CardBody, CardHeader } from "@heroui/react";

export default function HomeDashboardPage() {
  const pageTitle = "hello";

  return (
    <>
      <AppHeader header={<WorkspaceDashboardHeader />} />
      <ContentWrapper>
        <div className="p-5 flex flex-col vertical-scrollbar scrollbar-lg h-full w-full overflow-y-auto gap-6 bg-custom-background-90/20 vertical-scrollbar scrollbar-lg">
          <div className="flex justify-between">
            <div>
              <h3 className="text-xl font-semibold text-center">Good afternoon, Salvador Sebastian</h3>
              <h6 className="flex items-center gap-2 font-medium text-custom-text-400">
                <div>🌥️</div>
                <div>Wednesday, Jun 11 19:32</div>
              </h6>
            </div>
            <button
              type="button"
              className="text-custom-text-200 bg-custom-background-100 border border-custom-border-200 hover:bg-custom-background-90 focus:text-custom-text-300 focus:bg-custom-background-90 px-3 py-1.5 font-medium text-xs rounded flex items-center gap-1.5 whitespace-nowrap transition-all justify-center my-auto mb-0"
            >
              <Shapes size={16} />
              <div className="text-xs font-medium">Manage widgets</div>
            </button>
          </div>
          <div>
            <div>top</div>
            <div>bottom</div>
          </div>
        </div>
      </ContentWrapper>
    </>
  );
}
