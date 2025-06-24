"use client";
// React
import { FC, useEffect, useRef, useState } from "react";
// nextjs
import Link from "next/link";
import { usePathname } from "next/navigation";
// third-party
import { addToast } from "@heroui/toast";
import { Button } from "@heroui/button";
import { Activity, Bell, ChevronLeft, CircleUser, KeyRound, LogOut, MoveLeft, Settings2 } from "lucide-react";
import { Listbox, ListboxItem } from "@heroui/listbox";
// hooks
import { useAppTheme } from "@/hooks/use-app-theme";
import { useOutsideClickDetector } from "@/hooks/use-outside-click-detector";
import { usePlatformOS } from "@/hooks/use-platform-os";
import { useUser } from "@/hooks/use-user";

export const PROFILE_ACTION_LINKS: {
  key: string;
  href: string;
  highlight: (pathname: string) => boolean;
  isActive: boolean;
}[] = [
  {
    key: "profile",
    href: `/profile`,
    highlight: (pathname: string) => pathname === "/profile",
    isActive: false,
  },
  {
    key: "security",
    href: `/profile/security`,
    highlight: (pathname: string) => pathname === "/profile/security",
    isActive: false,
  },
  {
    key: "activity",
    href: `/profile/activity`,
    highlight: (pathname: string) => pathname === "/profile/activity",
    isActive: false,
  },
  {
    key: "appearance",
    href: `/profile/appearance`,
    highlight: (pathname: string) => pathname.includes("/profile/appearance"),
    isActive: false,
  },
  {
    key: "notifications",
    href: `/profile/notifications`,
    highlight: (pathname: string) => pathname === "/profile/notifications",
    isActive: false,
  },
];

const ProfileActionIcons = ({ type, size, className }: { type: string; size?: number; className?: string }) => {
  const icons = {
    profile: CircleUser,
    security: KeyRound,
    activity: Activity,
    appearance: Settings2,
    notifications: Bell,
  };
  if (type === undefined) return null;
  const Icon = icons[type as keyof typeof icons];
  return <Icon size={size} className={className} />;
};

export const ProfileLayoutSidebar = () => {
  // states
  const [isSigningOut, setIsSigningOut] = useState(false);
  // router
  const pathname = usePathname();
  // store hooks
  const { sidebarCollapsed, toggleSidebar } = useAppTheme();
  const { data: currentUser, signOut } = useUser();
  //   const { data: currentUserSettings } = useUserSettings();
  //   const { workspaces } = useWorkspace();
  // const { isMobile } = usePlatformOS();
  //   const { t } = useTranslation();

  const ref = useRef<HTMLDivElement>(null);

  useOutsideClickDetector(ref, () => {
    if (sidebarCollapsed === false) {
      if (window.innerWidth < 768) {
        toggleSidebar();
      }
    }
  });

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth <= 768) {
        toggleSidebar(true);
      }
    };
    handleResize();
    window.addEventListener("resize", handleResize);
    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, [toggleSidebar]);

  const handleItemClick = () => {
    if (window.innerWidth < 768) {
      toggleSidebar();
    }
  };

  const handleSignOut = async () => {
    setIsSigningOut(true);
    await signOut()
      .catch(() =>
        addToast({
          title: "Error!",
          description: "something went wrong",
          color: "danger",
        })
      )
      .finally(() => setIsSigningOut(false));
  };

  const navItemsWithActive = PROFILE_ACTION_LINKS.map((link) => ({
    ...link,
    isActive: link.highlight(pathname),
  }));

  return (
    <div
      className={`fixed inset-y-0 z-20 flex h-full flex-shrink-0 flex-grow-0 border-r border-custom-sidebar-border-200 bg-custom-sidebar-background-100 duration-300 md:relative
        ${sidebarCollapsed ? "-ml-[250px]" : ""}
        sm:${sidebarCollapsed ? "-ml-[250px]" : ""}
        md:ml-0 ${sidebarCollapsed ? "w-[70px]" : "w-[250px]"}
        `}
    >
      <div ref={ref} className="flex h-full w-full flex-col gap-y-4">
        <Link href={`/home`} onClick={handleItemClick}>
          <div
            className={`flex flex-shrink-0 items-center gap-2 truncate px-4 pt-4 ${
              sidebarCollapsed ? "justify-center" : ""
            }`}
          >
            <span className="grid h-5 w-5 flex-shrink-0 place-items-center">
              <ChevronLeft className="h-5 w-5" strokeWidth={1} />
            </span>
            {!sidebarCollapsed && (
              <h4 className="truncate text-lg font-semibold text-custom-text-200">Profile settings</h4>
            )}
          </div>
        </Link>
        <div className="flex flex-shrink-0 flex-col overflow-x-hidden">
          {!sidebarCollapsed && <h6 className="rounded px-6 text-sm font-semibold">Your account</h6>}
          <div className="vertical-scrollbar scrollbar-sm mt-2 px-4 h-full space-y-1 overflow-y-auto">
            <Listbox
              aria-label="Profile Actions"
              items={navItemsWithActive}
              classNames={{
                list: "gap-2",
              }}
              itemClasses={{
                base: `${sidebarCollapsed ? "flex-col justify-center items-center" : "flex"}`,
              }}
              onAction={(key) => handleItemClick()}
            >
              {(item) => (
                <ListboxItem
                  key={item.key}
                  className={`${item.highlight(pathname) ? "bg-green-400" : ""}`}
                  href={item.href}
                  textValue={item.key}
                >
                  <div className="flex items-center gap-2">
                    <ProfileActionIcons type={item.key} size={16} />
                    {!sidebarCollapsed && <p>{item.key}</p>}
                  </div>
                </ListboxItem>
              )}
            </Listbox>
          </div>
        </div>
        <div className="flex flex-shrink-0 flex-grow items-end px-6 py-2">
          <div
            className={`flex w-full ${
              sidebarCollapsed ? "flex-col justify-center items-center gap-2" : "items-center justify-between gap-2"
            }`}
          >
            <Button
              type="button"
              color="danger"
              variant="light"
              size="sm"
              startContent={<LogOut className="h-3.5 w-3.5" />}
              onPress={handleSignOut}
              isIconOnly={sidebarCollapsed ? true : false}
            >
              {!sidebarCollapsed && <span>{isSigningOut ? "Signing out..." : "Sign out"}</span>}
            </Button>
            <Button type="button" className="" onPress={() => toggleSidebar()} isIconOnly size="sm" variant="light">
              <MoveLeft className={`h-3.5 w-3.5 duration-300 ${sidebarCollapsed ? "rotate-180" : ""}`} />
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};
