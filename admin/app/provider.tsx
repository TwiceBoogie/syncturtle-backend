"use client";

import { FC, ReactNode } from "react";
import { HeroUIProvider, ToastProvider } from "@heroui/react";
import { SWRConfig } from "swr";

export interface IAppProvider {
  children: ReactNode;
}

const DEFAULT_SWR_CONFIG = {
  refreshWhenHidden: false,
  revalidateIfStale: false,
  revalidateOnFocus: false,
  revalidateOnMount: true,
  refreshInterval: 600000,
  errorRetryCount: 3,
};

export const AppProvider: FC<IAppProvider> = (props) => {
  const { children } = props;
  return (
    <HeroUIProvider>
      <ToastProvider />
      <SWRConfig value={DEFAULT_SWR_CONFIG}>{children}</SWRConfig>
    </HeroUIProvider>
  );
};
