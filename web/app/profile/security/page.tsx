"use client";
// @heroui
import { Form } from "@heroui/react";
// components
import { PageHead } from "@/components/core";
import { ProfileSettingContentWrapper } from "@/components/profile";

export default function SecurityPage() {
  return (
    <>
      <PageHead title="Profile - Security" />
      <ProfileSettingContentWrapper>
        <div className="flex flex-col gap-1 py-4 border-b border-custom-border-100">
          <div className="text-xl font-medium text-custom-text-100">Change password</div>
          {/* {description && <div className="text-sm font-normal text-custom-text-300">{description}</div>} */}
        </div>
        <Form className="gap-8 py-6">hi</Form>
      </ProfileSettingContentWrapper>
    </>
  );
}
