import { StoreContext } from "@/lib/store-context";
import { useContext, useSyncExternalStore } from "react";

export function useProfile() {
  const context = useContext(StoreContext);
  if (!context) throw new Error("useProfile must be used within a StoreProvider");
  const snapshot = useSyncExternalStore(
    context.user.userProfile.subscribe,
    context.user.userProfile.getSnapshot,
    context.user.userProfile.getServerSnapshot
  );
  return {
    data: snapshot.data,
    isLoading: snapshot.isLoading,
    error: snapshot.error,
    fetchUserProfile: context.user.userProfile.fetchUserProfile,
  };
}
