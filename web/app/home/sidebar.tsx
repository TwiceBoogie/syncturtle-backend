"use client";

import { FC, useEffect, useRef } from "react";
import { useAppTheme } from "@/hooks/use-app-theme";
import { useOutsideClickDetector } from "@/hooks/use-outside-click-detector";
import useSize from "@/hooks/use-window-size";
import { cn } from "@/helpers/common.helper";
import { SidebarDropdown, SidebarQuickActions } from "@/components/workspace/sidebar";
import { Button } from "@heroui/react";
import { ChevronsLeft, MoveLeft } from "lucide-react";
import { SidebarMenuItems } from "@/components/workspace/sidebar/sidebar-menu-items";

export const AppSidebar: FC = () => {
  const { toggleSidebar, sidebarCollapsed } = useAppTheme();
  const windowSize = useSize();
  // refs
  const ref = useRef<HTMLDivElement>(null);

  useOutsideClickDetector(ref, () => {
    if (sidebarCollapsed === false) {
      if (window.innerWidth < 768) {
        toggleSidebar();
      }
    }
  });
  useEffect(() => {
    if (windowSize[0] < 768 && !sidebarCollapsed) toggleSidebar();
  }, [windowSize]);

  const isCollapsed = sidebarCollapsed || false;

  return (
    <>
      {/* The actual sidebar */}
      <div
        className={cn(
          "fixed inset-y-0 z-20 flex h-full flex-shrink-0 flex-grow-0 flex-col border-r border-custom-sidebar-border-200 bg-custom-sidebar-background-100 duration-300 w-[250px] md:relative md:ml-0",
          {
            "w-[70px] -ml-[250px]": sidebarCollapsed,
          }
        )}
      >
        <div
          ref={ref}
          className={cn("size-full flex flex-col flex-1 pt-4 pb-0", {
            "p-2 pt-4": sidebarCollapsed,
          })}
        >
          <div
            className={cn("px-2", {
              "px-4": !sidebarCollapsed,
            })}
          >
            <SidebarDropdown />
            <div className="flex-shrink-0 h-4" />
            {/* this the sidebar stuff */}
            <SidebarQuickActions />
          </div>
          <hr
            className={cn("flex-shrink-0 border-custom-sidebar-border-300 h-[0.5px] w-3/5 mx-auto my-1", {
              "opacity-0": !sidebarCollapsed,
            })}
          />
          <div
            className={cn("overflow-x-hidden scrollbar-sm h-full w-full overflow-y-auto px-2 py-0.5", {
              "vertical-scrollbar px-4": !sidebarCollapsed,
            })}
          >
            <SidebarMenuItems />
            {sidebarCollapsed && (
              <hr className="flex-shrink-0 border-custom-sidebar-border-300 h-[0.5px] w-3/5 mx-auto my-1" />
            )}
          </div>
          <div
            className={cn(
              "flex w-full items-center justify-end px-2 self-baseline border-t border-custom-border-200 bg-custom-sidebar-background-100 h-12 flex-shrink-0",
              {
                "flex-col h-auto py-1.5": isCollapsed,
              }
            )}
          >
            <Button type="button" className="" onPress={() => toggleSidebar()} isIconOnly size="sm" variant="light">
              <MoveLeft className={`h-3.5 w-3.5 duration-300 ${sidebarCollapsed ? "rotate-180" : ""}`} />
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};
