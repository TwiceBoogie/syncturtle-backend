import { useCallback } from "react";
import { usePasswordStore } from "./use-password";
import { useUserStore } from "./use-user";
import { logout } from "@/actions/auth-logout";

export function useRootStore() {
  const userStore = useUserStore();
  const passwordStore = usePasswordStore();

  const resetAll = useCallback(async (): Promise<void> => {
    await logout();
    userStore.dispatch({ type: "LOGOUT" });
    passwordStore.dispatch({ type: "RESET" });
  }, [userStore, passwordStore]);

  return {
    resetAll,
  };
}
