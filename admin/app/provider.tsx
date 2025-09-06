"use client";

import { FC, ReactNode } from "react";
import { HeroUIProvider, ToastProvider } from "@heroui/react";

export interface IAppProvider {
  children: ReactNode;
}

export const AppProvider: FC<IAppProvider> = (props) => {
  const { children } = props;
  return (
    <HeroUIProvider>
      <ToastProvider />
      {children}
    </HeroUIProvider>
  );
};
