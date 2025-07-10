import { EPageTypes } from "@/helpers/authentication.helper";
import { AuthenticationWrapper } from "@/lib/wrappers";
import { Metadata } from "next";

export const metadata: Metadata = {
  title: "New Device Detected - SyncTurtle",
};

export default function NewDeviceLayout({ children }: { children: React.ReactNode }) {
  return <AuthenticationWrapper pageType={EPageTypes.NON_AUTHENTICATED}>{children}</AuthenticationWrapper>;
}
