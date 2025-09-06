"use client";

import { FC, ReactNode } from "react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import useSWR from "swr";
import { Spinner } from "@heroui/react";
import { EPageTypes } from "@/helpers/authentication.helper";
import { useProfile, useUser } from "@/hooks/store";

type TPageType = EPageTypes;

type TAuthenticationWrapper = {
  children: ReactNode;
  pageType?: TPageType;
};

const isValidURL = (url: string): boolean => {
  const disallowedSchemes = /^(https?|ftp):\/\//i;
  return !disallowedSchemes.test(url);
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
  const { data: currentUserProfile } = useProfile();

  const { isLoading: isUserSWRLoading } = useSWR("USER_INFORMATION", async () => await fetchCurrentUser(), {
    revalidateOnFocus: false,
    shouldRetryOnError: false,
  });

  const isUserOnboarded =
    currentUserProfile?.isOnboarded ||
    (currentUserProfile?.onboardingStep.profileComplete &&
      currentUserProfile?.onboardingStep.workspaceCreated &&
      currentUserProfile.onboardingStep.workspaceInvite &&
      currentUserProfile.onboardingStep.workspaceJoin) ||
    false;

  if ((isUserSWRLoading || isUserLoading) && !currentUser?.id) {
    return (
      <div className="relative flex h-screen w-full items-center justify-center">
        <Spinner />
      </div>
    );
  }

  const getWorkspaceRedirectionUrl = (): string => {
    let redirectionRoute = "/profile";

    if (nextPath && isValidURL(nextPath.toString())) {
      redirectionRoute = nextPath.toString();
      return redirectionRoute;
    }

    return redirectionRoute;
  };

  if (pageType === EPageTypes.PUBLIC) return <>{children}</>;

  if (pageType === EPageTypes.NON_AUTHENTICATED) {
    if (!currentUser?.id) return <>{children}</>;
    else {
      if (currentUserProfile?.id && isUserOnboarded) {
        const currentRedirectRoute = getWorkspaceRedirectionUrl();
        router.push(currentRedirectRoute);
        return <></>;
      } else {
        router.push("/onboarding");
        return <></>;
      }
    }
  }

  if (pageType === EPageTypes.ONBOARDING) {
    if (!currentUser?.id) {
      router.push(`/${pathname ? `?next_path=${pathname}` : ``}`);
      return <></>;
    } else {
      if (currentUser && currentUserProfile?.id && isUserOnboarded) {
        const currentRedirectRoute = getWorkspaceRedirectionUrl();
        router.replace(currentRedirectRoute);
        return <></>;
      } else {
        return <>{children}</>;
      }
    }
  }

  // if (pageType === EPageTypes.SET_PASSWORD) {
  //   if (!currentUser?.id) {
  //     router.push(`/${pathname ? `?next_path=${pathname}` : ``}`);
  //     return <></>;
  //   } else {
  //     if (currentUser && !currentUser?.is_password_autoset && currentUserProfile?.id && isUserOnboard) {
  //       const currentRedirectRoute = getWorkspaceRedirectionUrl();
  //       router.push(currentRedirectRoute);
  //       return <></>;
  //     } else return <>{children}</>;
  //   }
  // }

  if (pageType === EPageTypes.AUTHENTICATED) {
    if (currentUser?.id) {
      if (currentUserProfile && currentUserProfile?.id && isUserOnboarded) return <>{children}</>;
      else {
        router.push(`/onboarding`);
        return <></>;
      }
    } else {
      router.push(`/${pathname ? `?next_path=${pathname}` : ``}`);
      return <></>;
    }
  }

  return <>{children}</>;
};
