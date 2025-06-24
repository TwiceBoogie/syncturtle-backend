import { PasswordSidebarRoot } from "@/components/passwords/root";
import React, { ReactNode } from "react";

export default function layout({ children }: { children: ReactNode }) {
  return (
    <div className="relative w-full h-full overflow-hidden flex items-center">
      <PasswordSidebarRoot />
      <div className="w-full h-full overflow-hidden overflow-y-auto">{children}</div>
    </div>
  );
}
