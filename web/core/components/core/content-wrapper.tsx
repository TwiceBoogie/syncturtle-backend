"use client";
// react
import { ReactNode } from "react";
// @heroui
import { ScrollShadow } from "@heroui/scroll-shadow";
// helpers
import { cn } from "@/helpers/common.helper";

export interface ContentWrapperProps {
  className?: string;
  children: ReactNode;
}

export const ContentWrapper = ({ className, children }: ContentWrapperProps) => (
  <div className="h-full w-full overflow-hidden">
    <ScrollShadow size={0} className={cn("relative h-full w-full ", className)}>
      {children}
    </ScrollShadow>
  </div>
);
