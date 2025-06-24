"use client";

import { FC, ReactNode } from "react";
import { UserProvider } from "@/lib/context/user-context";
import { HeroUIProvider, ToastProvider } from "@heroui/react";
import { ThemeProvider as CustomThemeProvider } from "@/lib/context/theme-context";
import { useRouter } from "next/navigation";

export interface IAppProvider {
  children: ReactNode;
}

declare module "@react-types/shared" {
  interface RouterConfig {
    routerOptions: NonNullable<Parameters<ReturnType<typeof useRouter>["push"]>[1]>;
  }
}

export const AppProvider: FC<IAppProvider> = (props) => {
  const { children } = props;
  const router = useRouter();

  return (
    <UserProvider>
      <CustomThemeProvider>
        <HeroUIProvider navigate={router.push}>
          <ToastProvider />
          {children}
        </HeroUIProvider>
      </CustomThemeProvider>
    </UserProvider>
  );
};
