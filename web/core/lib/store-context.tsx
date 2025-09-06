"use client";

import { createContext, ReactNode } from "react";
import { RootStore } from "@/store/root.store";

// instantiate store
export let rootStore = new RootStore();
// store rootStore inside react context
export const StoreContext = createContext<RootStore>(rootStore);

const initStore = () => {
  const newRootStore = rootStore ?? new RootStore();
  if (typeof window === "undefined") return newRootStore;
  if (!rootStore) rootStore = newRootStore;
  return newRootStore;
};

export const store = initStore();

export const StoreProvider = ({ children }: { children: ReactNode }) => {
  return <StoreContext.Provider value={store}>{children}</StoreContext.Provider>;
};
