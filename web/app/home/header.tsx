"use client";

import Image from "next/image";
import { Home } from "lucide-react";
// images
import githubBlackImage from "@/public/logos/github-black.png";
import githubWhiteImage from "@/public/logos/github-white.png";
// ui
import { Header } from "@/components/core/ui/header";
// components
import { Breadcrumbs, BreadcrumbItem } from "@heroui/breadcrumbs";
// constants

export const WorkspaceDashboardHeader = () => {
  return (
    <>
      <Header>
        <Header.LeftItem>
          <div>
            <Breadcrumbs>
              <BreadcrumbItem>Home</BreadcrumbItem>
              <BreadcrumbItem>Music</BreadcrumbItem>
              <BreadcrumbItem>Artist</BreadcrumbItem>
              <BreadcrumbItem>Album</BreadcrumbItem>
              <BreadcrumbItem>Song</BreadcrumbItem>
            </Breadcrumbs>
            {/* <Breadcrumbs>
              <BreadcrumbItem>Home</BreadcrumbItem>
              <Breadcrumbs.BreadcrumbItem
                type="text"
                link={
                  <BreadcrumbLink label={"hello"} icon={<Home className="h-4 w-4 text-custom-text-300" />} />
                }
              />
            </Breadcrumbs> */}
          </div>
        </Header.LeftItem>
        <Header.RightItem>
          <a
            className="flex flex-shrink-0 items-center gap-1.5 rounded bg-custom-background-80 px-3 py-1.5"
            href="https://github.com/makeplane/plane"
            target="_blank"
            rel="noopener noreferrer"
          >
            <Image src={githubWhiteImage} height={16} width={16} alt="GitHub Logo" />
            <span className="hidden text-xs font-medium sm:hidden md:block">hello</span>
          </a>
        </Header.RightItem>
      </Header>
    </>
  );
};
