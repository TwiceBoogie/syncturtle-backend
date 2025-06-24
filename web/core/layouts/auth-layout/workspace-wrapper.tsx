"use client";

import { FC } from "react";
import { usePlatformOS } from "@/hooks/use-platform-os";
import { useUser } from "@/hooks/use-user";
import PlaneBlackLogo from "@/public/plane-logos/black-horizontal-with-blue-logo.png";
import PlaneWhiteLogo from "@/public/plane-logos/white-horizontal-with-blue-logo.png";
import WorkSpaceNotAvailable from "@/public/workspace/workspace-not-available.png";
import { addToast } from "@heroui/react";

interface IWorkspaceAuthWrapper {
  children: React.ReactNode;
  isLoading?: boolean;
}

export const WorkspaceAuthWrapper: FC<IWorkspaceAuthWrapper> = (props) => {
  const { children, isLoading: isParentLoading = false } = props;
  // context hooks
  const { signOut, data: currentUser } = useUser();
  const { isMobile } = usePlatformOS();

  const handleSignOut = async () => {
    await signOut().catch(() =>
      addToast({
        title: "Error!",
        description: "Failed to sign out. Please try again.",
        color: "danger",
      })
    );
  };

  return <>{children}</>;
};
