"use client";

import { EPageTypes } from "@/helpers/authentication.helper";
import { useUser } from "@/hooks/use-user";
import { Spinner } from "@heroui/react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { FC, ReactNode, useEffect } from "react";
import useSWR from "swr";

type TPageType = EPageTypes;

type TAuthenticationWrapper = {
  children: ReactNode;
  pageType?: TPageType;
};

export const AuthenticationWrapper: FC<TAuthenticationWrapper> = (props) => {
  const pathname = usePathname();
  const router = useRouter();
  const searchParams = useSearchParams();
  const nextPath = searchParams.get("next_path");
  // props
  const { children, pageType = EPageTypes.AUTHENTICATED } = props;
  // hooks
  const { isLoading: isUserLoading, data: currentUser, fetchCurrentUser } = useUser();

  const { isLoading: isUserSWRLoading } = useSWR("USER_INFORMATION", async () => await fetchCurrentUser(), {
    revalidateOnFocus: false,
    shouldRetryOnError: false,
  });

  if ((isUserSWRLoading || isUserLoading) && !currentUser?.id) {
    return (
      <div className="relative flex h-screen w-full items-center justify-center">
        <Spinner />
      </div>
    );
  }

  if (pageType === EPageTypes.PUBLIC) return <>{children}</>;

  if (pageType === EPageTypes.NON_AUTHENTICATED) {
    console.log(currentUser);
    if (!currentUser?.id) return <>{children}</>;
    else {
      router.push("/home");
      return <></>;
    }
  }

  if (pageType === EPageTypes.AUTHENTICATED) {
    if (currentUser?.id) {
      return <>{children}</>;
    } else {
      router.push(`/${pathname ? `?next_path=${pathname}` : ``}`);
    }
  }

  return <>{children}</>;
};
