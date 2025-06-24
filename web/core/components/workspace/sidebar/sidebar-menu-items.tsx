import { useEffect, useMemo, useRef, useState } from "react";
// heroui
import { type Selection } from "@heroui/react";
import { Listbox, ListboxSection, ListboxItem } from "@heroui/listbox";
// hooks
import { useAppTheme } from "@/hooks/use-app-theme";
// helpers
import { cn } from "@/helpers/common.helper";
import { usePathname } from "next/navigation";
import { Activity, Bell, Home, KeySquare, Settings2 } from "lucide-react";
import Link from "next/link";

const HOME_ACTION_LINKS: {
  key: string;
  href: string;
  highlight: (pathname: string) => boolean;
  isActive: boolean;
}[] = [
  {
    key: "home",
    href: `/home`,
    highlight: (pathname: string) => pathname === "/home",
    isActive: false,
  },
  {
    key: "passwords",
    href: `/home/passwords`,
    highlight: (pathname: string) => pathname === "/home/passwords",
    isActive: false,
  },
  {
    key: "activity",
    href: `/home/activity`,
    highlight: (pathname: string) => pathname === "/home/activity",
    isActive: false,
  },
  {
    key: "appearance",
    href: `/home/appearance`,
    highlight: (pathname: string) => pathname.includes("/home/appearance"),
    isActive: false,
  },
  {
    key: "notifications",
    href: `/home/notifications`,
    highlight: (pathname: string) => pathname === "/home/notifications",
    isActive: false,
  },
];

const HomeActionIcons = ({ type, size, className }: { type: string; size?: number; className?: string }) => {
  const icons = {
    home: Home,
    passwords: KeySquare,
    activity: Activity,
    appearance: Settings2,
    notifications: Bell,
  };
  if (type === undefined) return null;
  const Icon = icons[type as keyof typeof icons];
  return <Icon size={size} className={className} />;
};

export const SidebarMenuItems = () => {
  const pathname = usePathname();

  const { sidebarCollapsed, toggleExtendedSidebar } = useAppTheme();

  const navItemsWithActive = HOME_ACTION_LINKS.map((link) => ({
    ...link,
    isActive: link.highlight(pathname),
  }));
  return (
    <div
      className={cn("flex flex-col gap-0.5", {
        "space-y-0": sidebarCollapsed,
      })}
    >
      <Listbox
        aria-label="Actions"
        items={navItemsWithActive}
        itemClasses={{
          base: cn(
            "h-8", // fixed height for alignment consistency
            "data-[hover=true]:bg-green-400",
            "data-[selectable=true]:focus:bg-green-400",
            "data-[selected=true]:bg-green-400",

            {
              "flex justify-center items-center": sidebarCollapsed, // full centering
            }
          ),
        }}
      >
        {(item) => (
          <ListboxItem
            key={item.key}
            className={`${item.highlight(pathname) ? "bg-green-400" : ""}`}
            href={item.href}
            textValue={item.key}
          >
            <div className="flex items-center gap-2">
              <HomeActionIcons type={item.key} size={16} />
              {!sidebarCollapsed && <p>{item.key}</p>}
            </div>
          </ListboxItem>
        )}
      </Listbox>
    </div>
  );
};
