import { cn } from "@/helpers/common.helper";
import { useAppTheme } from "@/hooks/use-app-theme";
import { useUser } from "@/hooks/use-user";
import { Dropdown, DropdownItem, DropdownMenu, DropdownTrigger } from "@heroui/dropdown";
import { addToast } from "@heroui/toast";
import { Avatar } from "@heroui/avatar";
import { ChevronDown, CirclePlus, LogOut, Mails, Menu, Settings } from "lucide-react";
import Link from "next/link";
import { Button } from "@heroui/button";

export interface IWorkspace {
  readonly id: string;
  //   readonly owner: IUser;
  readonly created_at: Date;
  readonly updated_at: Date;
  name: string;
  url: string;
  logo_url: string | null;
  readonly total_members: number;
  readonly slug: string;
  readonly created_by: string;
  readonly updated_by: string;
  organization_size: string;
  total_projects?: number;
  role: number;
}

export const SidebarDropdown = () => {
  // store hooks
  const { sidebarCollapsed, toggleSidebar } = useAppTheme();
  const { signOut, data: currentUser } = useUser();
  const items = [
    {
      key: "new",
      label: "New file",
    },
    {
      key: "copy",
      label: "Copy link",
    },
    {
      key: "edit",
      label: "Edit file",
    },
    {
      key: "delete",
      label: "Delete file",
    },
  ];

  const handleSignOut = async () => {
    await signOut().catch(() => {
      addToast({
        title: "Error!",
        description: "Sign out failed. Please try again.",
        color: "danger",
      });
    });
  };

  const handleItemClick = () => {
    if (window.innerWidth < 768) {
      toggleSidebar();
    }
  };

  return (
    <div
      className={cn("flex items-center justify-between", {
        "flex-col gap-y-3": sidebarCollapsed,
      })}
    >
      <Dropdown>
        <DropdownTrigger>
          {sidebarCollapsed ? (
            <Button
              type="button"
              isIconOnly
              size="sm"
              className="group flex-shrink-0 size-7 grid place-items-center rounded bg-custom-background-80 transition-all hover:bg-custom-background-90"
            >
              <Menu className="size-3.5 text-custom-text-200 transition-all group-hover:text-custom-text-100" />
            </Button>
          ) : (
            <Button size="sm">Open Menu</Button>
          )}
        </DropdownTrigger>
        <DropdownMenu aria-label="Dynamic Actions" items={items}>
          {(item) => (
            <DropdownItem
              key={item.key}
              className={item.key === "delete" ? "text-danger" : ""}
              color={item.key === "delete" ? "danger" : "default"}
            >
              {item.label}
            </DropdownItem>
          )}
        </DropdownMenu>
      </Dropdown>
      <Dropdown
        placement="right"
        classNames={{
          base: "before:bg-default-200",
          content: "p-0 border-small border-divider bg-background",
        }}
        radius="sm"
      >
        <DropdownTrigger>
          <Avatar src="https://i.pravatar.cc/150?u=a042581f4e29026704d" as="button" size="sm" />
        </DropdownTrigger>
        <DropdownMenu
          aria-label="Profile Actions"
          disabledKeys={["profile"]}
          itemClasses={{
            base: [
              "rounded-md",
              "text-default-500",
              "transition-opacity",
              "data-[hover=true]:text-foreground",
              "data-[hover=true]:bg-default-100",
              "dark:data-[hover=true]:bg-default-50",
              "data-[selectable=true]:focus:bg-default-50",
              "data-[pressed=true]:opacity-70",
              "data-[focus-visible=true]:ring-default-500",
            ],
          }}
        >
          <DropdownItem key="profile" isReadOnly textValue="profile">
            <p className="text-xs ">salvadorsebastian@live.com</p>
          </DropdownItem>
          <DropdownItem key="settings" href="/profile" textValue="settings">
            Settings
          </DropdownItem>
          <DropdownItem key="signOut" textValue="Sign out">
            Sign-Out
          </DropdownItem>
        </DropdownMenu>
      </Dropdown>
    </div>
  );
};
