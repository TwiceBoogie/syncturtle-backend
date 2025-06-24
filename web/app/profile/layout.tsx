import { AuthenticationWrapper } from "@/lib/wrappers";
import React from "react";
import { ProfileLayoutSidebar } from "./sidebar";

export default function layout({ children }: { children: React.ReactNode }) {
  return (
    <AuthenticationWrapper>
      <div className="relative flex h-full w-full overflow-hidden">
        <ProfileLayoutSidebar />
        <main className="relative flex h-full w-full flex-col overflow-hidden bg-custom-background-100">
          <div className="h-full w-full overflow-hidden">{children}</div>
        </main>
      </div>
    </AuthenticationWrapper>
  );
}
