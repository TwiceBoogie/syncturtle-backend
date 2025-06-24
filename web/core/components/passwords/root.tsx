"use client";

import { FC, useCallback, useState } from "react";
import { ERowVariant, Row } from "@/components/core/ui/row";
import { cn } from "@/helpers/common.helper";
import { PasswordSidebarHeader } from "./header";
import { EHeaderVariant, Header } from "@/components/core/ui/header";
import { Listbox, ListboxSection, ListboxItem } from "@heroui/listbox";
import { ContentWrapper } from "@/components/core/ui/content-wrapper";

export enum EPasswordTab {
  ALL = "all",
  RECENT = "recent",
}

export const PASSWORD_TABS = [
  {
    label: "All",
    value: EPasswordTab.ALL,
  },
  {
    label: "Recent",
    value: EPasswordTab.RECENT,
  },
];

export type TPassword_Tab = EPasswordTab.ALL | EPasswordTab.RECENT;

export const PasswordSidebarRoot: FC = () => {
  const [currentPasswordTab, setCurrentPasswordTab] = useState(EPasswordTab.ALL);
  const handleTabClick = useCallback(
    (tabValue: TPassword_Tab) => {
      if (currentPasswordTab !== tabValue) {
        setCurrentPasswordTab(tabValue);
      }
    },
    [currentPasswordTab, setCurrentPasswordTab]
  );
  const currentSelectedNotificationId = false;
  return (
    <div
      className={cn(
        "relative border-0 md:border-r border-custom-border-200 z-[10] flex-shrink-0 bg-custom-background-100 h-full transition-all overflow-hidden",
        currentSelectedNotificationId ? "w-0 md:w-2/6" : "w-full md:w-2/6"
      )}
    >
      <div className="relative w-full h-full overflow-hidden flex flex-col">
        <Row className="h-[3.75rem] border-b border-custom-border-200 flex">
          <PasswordSidebarHeader />
        </Row>

        <Header variant={EHeaderVariant.SECONDARY} className="justify-start">
          {PASSWORD_TABS.map((tab) => (
            <div
              key={tab.value}
              className="h-full px-3 relative cursor-pointer"
              onClick={() => handleTabClick(tab.value)}
            >
              <div
                className={cn(
                  `relative h-full flex justify-center items-center gap-1 text-sm transition-all`,
                  currentPasswordTab === tab.value ? "text-amber-300" : "text-green-400 hover:text-green-500"
                )}
              >
                <div className="font-medium">{tab.label}</div>
              </div>
              {currentPasswordTab === tab.value && (
                <div className="border absolute bottom-0 right-0 left-0 rounded-t-md border-green-300" />
              )}
            </div>
          ))}
        </Header>
        <ContentWrapper variant={ERowVariant.HUGGING}>
          <Listbox aria-label="Password list">
            <ListboxItem>hello</ListboxItem>
          </Listbox>
        </ContentWrapper>
      </div>
    </div>
  );
};
