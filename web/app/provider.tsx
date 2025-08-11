"use client";

import { FC, ReactNode } from "react";
import { UserProvider } from "@/lib/context/user-context";
import { HeroUIProvider, ToastProvider } from "@heroui/react";
import { ThemeProvider as CustomThemeProvider } from "@/lib/context/theme-context";
import { useRouter } from "next/navigation";
import { SWRConfig } from "swr";
import { PasswordProvider } from "@/lib/context/password-context";
import { InstanceProvider } from "@/lib/context/instance-context";
import BuildProviderTree from "@/lib/providers/BuildProviderTree";
import { InstanceWrapper } from "@/lib/wrappers";

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
// module load (not initializing)
const providers = [
  (children: React.ReactNode) => <InstanceProvider>{children}</InstanceProvider>,
  (children: React.ReactNode) => <UserProvider>{children}</UserProvider>,
  (children: React.ReactNode) => <PasswordProvider>{children}</PasswordProvider>,
  (children: React.ReactNode) => <CustomThemeProvider>{children}</CustomThemeProvider>,
];

const ProviderTree = BuildProviderTree(providers);

export const AppProvider: FC<IAppProvider> = (props) => {
  const { children } = props;
  const router = useRouter();

  return (
    <ProviderTree>
      <HeroUIProvider navigate={router.push}>
        <ToastProvider />
        <InstanceWrapper>
          <SWRConfig value={WEB_SWR_CONFIG}>{children}</SWRConfig>
        </InstanceWrapper>
      </HeroUIProvider>
    </ProviderTree>
  );
};
