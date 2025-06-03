"use client";

import { FC, ReactNode } from "react";
import { UserProvider } from "@/lib/context/user-context";
import { HeroUIProvider } from "@heroui/react";

export interface IAppProvider {
  children: ReactNode;
}

export const AppProvider: FC<IAppProvider> = (props) => {
  const { children } = props;

  return (
    <UserProvider>
      <HeroUIProvider>{children}</HeroUIProvider>
    </UserProvider>
  );
};
