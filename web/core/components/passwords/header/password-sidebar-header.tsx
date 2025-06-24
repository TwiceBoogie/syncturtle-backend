import { FC } from "react";

import { SidebarHamburgerToggle } from "@/components/core/sidebar";
import { Header } from "@/components/core/ui/header";
import { BreadcrumbItem, Breadcrumbs } from "@heroui/breadcrumbs";

export const PasswordSidebarHeader: FC = () => {
  return (
    <Header className="my-auto">
      <Header.LeftItem>
        <div className="block bg-custom-sidebar-background-100 md:hidden">
          <SidebarHamburgerToggle />
        </div>
        <Breadcrumbs>
          <BreadcrumbItem>Home</BreadcrumbItem>
          <BreadcrumbItem>Passwords</BreadcrumbItem>
        </Breadcrumbs>
      </Header.LeftItem>
    </Header>
  );
};
