"use client";

import Image from "next/image";
import Link from "next/link";

import { EAuthModes, EPageTypes } from "@/helpers/authentication.helper";
import { AuthenticationWrapper } from "@/lib/wrappers/authentication-wrapper";
// assets
import PlaneBackgroundPatternDark from "@/public/auth/background-pattern-dark.svg";
import WhiteHorizontalLogo from "@/public/plane-logos/white-horizontal-with-blue-logo.png";
import { AuthRoot } from "@/components/account/auth-forms/auth-root";

export type AuthType = "sign-in" | "sign-up";

export default function SignIn() {
  return (
    <AuthenticationWrapper pageType={EPageTypes.NON_AUTHENTICATED}>
      <div className="relative w-screen h-screen overflow-hidden">
        <div className="absolute inset-0 z-0">
          <Image src={PlaneBackgroundPatternDark} className="w-full h-full object-cover" alt="background pattern" />
        </div>
        <div className="relative z-10 w-screen h-screen overflow-hidden overflow-y-auto flex flex-col">
          <div className="container min-w-full px-10 lg:px-20 xl:px-36 flex-shrink-0 relative flex items-center justify-between pb-4 transition-all">
            <div className="flex items-center gap-x-2 py-10">
              <Link href={`/`} className="h-[30px] w-[133px]">
                {/* <Image src={WhiteHorizontalLogo} alt="Plane logo" /> */}
                <h1 className="text-3xl">
                  Sync<span className="text-green-400">Turtle</span>
                </h1>
              </Link>
            </div>
            <div className="flex flex-col items-end sm:items-center sm:gap-2 sm:flex-row  text-center text-sm font-medium">
              Already have an account?
              <Link href="/" className="font-semibold hover:underline">
                Log in
              </Link>
            </div>
          </div>
          <div className="flex flex-col justify-center flex-grow container h-[100vh-60px] mx-auto max-w-lg px-10 lg:max-w-md lg:px-5 transition-all">
            <AuthRoot authMode={EAuthModes.SIGN_UP} />
          </div>
        </div>
      </div>
    </AuthenticationWrapper>
  );
}
