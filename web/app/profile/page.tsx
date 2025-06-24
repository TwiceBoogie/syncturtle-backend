"use client";
// @heroui
import { Spinner } from "@heroui/spinner";
// components
import { PageHead } from "@/components/core";
import { ProfileForm, ProfileSettingContentWrapper } from "@/components/profile";
// hooks
import { useUser } from "@/hooks/use-user";

export default function page() {
  const { data: currentUser } = useUser();

  // if (!currentUser) {
  //   return (
  //     <div className="grid h-full w-full place-items-center px-4 sm:px-0">
  //       <Spinner />
  //     </div>
  //   );
  // }
  return (
    <>
      <PageHead title={`Profile - General settings`} />
      <ProfileSettingContentWrapper>
        <ProfileForm />
      </ProfileSettingContentWrapper>
    </>
  );
}
