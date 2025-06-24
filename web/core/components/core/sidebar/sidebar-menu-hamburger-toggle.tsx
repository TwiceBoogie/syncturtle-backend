"use client";

import { Menu } from "lucide-react";
import { useAppTheme } from "@/hooks/use-app-theme";
import { Button } from "@heroui/react";

export const SidebarHamburgerToggle = () => {
  // store hooks
  const { toggleSidebar } = useAppTheme();

  return (
    <Button
      type="button"
      size="sm"
      isIconOnly
      className="group flex-shrink-0 size-7 grid place-items-center rounded bg-custom-background-80 transition-all hover:bg-custom-background-90 md:hidden"
      onPress={() => toggleSidebar()}
    >
      <Menu className="size-3.5 text-custom-text-200 transition-all group-hover:text-custom-text-100" />
    </Button>
  );
};
