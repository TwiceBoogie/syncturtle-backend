import { StoreContext } from "@/lib/store-context";
import { useContext, useSyncExternalStore } from "react";

export function useInstance() {
  const context = useContext(StoreContext);
  if (!context) throw new Error("useInstance must be used within a StoreProvider");
  const snapshot = useSyncExternalStore(
    context.instance.subscribe,
    context.instance.getSnapshot,
    context.instance.getServerSnapshot
  );
  return {
    instance: snapshot.instance,
    config: snapshot.config,
    isLoading: snapshot.isLoading,
    error: snapshot.error,
    fetchInstanceInfo: context.instance.fetchInstanceInfo,
  };
}
