"use client";

import { EPageTypes } from "@/helpers/authentication.helper";
import { useUser } from "@/hooks/useUser";
import { Spinner } from "@heroui/react";
import { usePathname, useSearchParams } from "next/navigation";
import { FC, ReactNode, useEffect } from "react";

type TPageType = EPageTypes;

type TAuthenticationWrapper = {
  children: ReactNode;
  pageType?: TPageType;
};

export const AuthenticationWrapper: FC<TAuthenticationWrapper> = (props) => {
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const nextPath = searchParams.get("next_path");
  // props
  const { children, pageType = EPageTypes.AUTHENTICATED } = props;
  // hooks
  const { isLoading: isUserLoading, user, fetchCurrentUser } = useUser();

  useEffect(() => {
    fetchCurrentUser();
  }, [fetchCurrentUser]);

  if (pageType === EPageTypes.PUBLIC) return <>{children}</>;

  if (pageType === EPageTypes.NON_AUTHENTICATED) {
    if (!user?.id) return <>{children}</>;
  }

  return <>{children}</>;
};
