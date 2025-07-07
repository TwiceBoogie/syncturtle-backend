"use client";

import { FC, ReactNode } from "react";
import { UserProvider } from "@/lib/context/user-context";
import { HeroUIProvider, ToastProvider } from "@heroui/react";
import { ThemeProvider as CustomThemeProvider } from "@/lib/context/theme-context";
import { useRouter } from "next/navigation";
import { SWRConfig } from "swr";
import { PasswordProvider } from "@/lib/context/password-context";

export interface IAppProvider {
  children: ReactNode;
}

export const WEB_SWR_CONFIG = {
  refreshWhenHidden: false,
  revalidateIfStale: true,
  revalidateOnFocus: true,
  revalidateOnMount: true,
  errorRetryCount: 3,
};

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
      <PasswordProvider>
        <CustomThemeProvider>
          <HeroUIProvider navigate={router.push}>
            <ToastProvider />
            <SWRConfig value={WEB_SWR_CONFIG}>{children}</SWRConfig>
          </HeroUIProvider>
        </CustomThemeProvider>
      </PasswordProvider>
    </UserProvider>
  );
};
