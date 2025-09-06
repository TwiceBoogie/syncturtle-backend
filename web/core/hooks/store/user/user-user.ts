import { StoreContext } from "@/lib/store-context";
import { useContext, useSyncExternalStore } from "react";

export function useUser() {
  const context = useContext(StoreContext);
  if (!context) throw new Error("useUser must be used within a StoreProvider");
  const snapshot = useSyncExternalStore(
    context.user.subscribe,
    context.user.getSnapshot,
    context.user.getServerSnapshot
  );
  return {
    data: snapshot.data,
    isLoading: snapshot.isLoading,
    error: snapshot.error,
    fetchCurrentUser: context.user.fetchCurrentUser,
  };
}
