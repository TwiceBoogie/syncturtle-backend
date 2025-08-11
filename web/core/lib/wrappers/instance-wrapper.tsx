"use client"; // if missing says 'useSWR' not found in target module

import { InstanceNotReady, MaintenanceView } from "@/components/instance";
import { useInstance } from "@/hooks/use-instance";
import { Spinner } from "@heroui/react";
import { FC, ReactNode } from "react";
import useSWR from "swr";

type TInstanceWrapper = {
  children: ReactNode;
};

export const InstanceWrapper: FC<TInstanceWrapper> = (props) => {
  const { children } = props;
  // store
  const { isLoading, instance, error, fetchInstanceInfo } = useInstance();

  const { isLoading: isInstanceSWRLoading, error: instanceSWRError } = useSWR(
    "INSTANCE_INFORMATION",
    async () => await fetchInstanceInfo(),
    { revalidateOnFocus: false }
  );

  if ((isLoading || isInstanceSWRLoading) && !instance) {
    return (
      <div className="relative flex h-screen w-full items-center justify-center">
        <Spinner />
      </div>
    );
  }

  if (instanceSWRError) return <MaintenanceView />;

  // instance is not ready and setup is not done
  if (error && error?.status === "error") {
    return <>{children}</>;
  }
  // instance is set but not setup
  console.log(instance);
  if (instance?.is_setup_done === false) return <InstanceNotReady />;

  return <>{children}</>;
};
