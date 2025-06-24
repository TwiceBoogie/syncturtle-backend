"use client";

import { AuthenticationWrapper } from "@/lib/wrappers";
import { AppSidebar } from "./sidebar";

export default function HomeLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthenticationWrapper>
      <div className="relative flex h-screen w-full overflow-hidden">
        <AppSidebar />
        <main className="relative flex h-full w-full flex-col overflow-hidden bg-custom-background-100">
          {children}
        </main>
      </div>
    </AuthenticationWrapper>
  );
}
