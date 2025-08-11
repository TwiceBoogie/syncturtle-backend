import InstanceContext from "@/lib/context/instance-context";
import { useContext } from "react";

export function useInstance() {
  const context = useContext(InstanceContext);
  if (!context) throw new Error("userInstance must be used within a InstanceProvider");
  return context;
}
