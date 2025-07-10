"use client";

import { EPageTypes } from "@/helpers/authentication.helper";
import { AuthenticationWrapper } from "@/lib/wrappers";
import Link from "next/link";
import PlaneBackgroundPatternDark from "@/public/auth/background-pattern-dark.svg";
import Image from "next/image";
import { Button, Form, Input } from "@heroui/react";
import { useSearchParams } from "next/navigation";
import { useState } from "react";
import useTimer from "@/hooks/use-timer";
import { CircleCheck } from "lucide-react";

type TUniqueCodeFormValues = {
  email: string;
  code: string;
};

const defaultValues: TUniqueCodeFormValues = {
  email: "",
  code: "",
};

export default function NewDeviceDetectedPage() {
  const searchParams = useSearchParams();
  // query params
  const emailParam = searchParams.get("email");
  const error_code = searchParams.get("error_code");
  // derived values
  const defaultResetTimerValue = 5;
  // states
  const [email, setEmail] = useState(emailParam ? emailParam.toString() : "");
  const [uniqueCodeFormData, setUniqueCodeFormData] = useState<TUniqueCodeFormValues>({ ...defaultValues, email });
  const [isRequestingNewCode, setIsRequestingNewCode] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  // timer
  const { timer: resendTimerCode, setTimer: setResendCodeTimer } = useTimer(0);
  console.log(resendTimerCode);

  const handleFormChange = (key: keyof TUniqueCodeFormValues, value: string) =>
    setUniqueCodeFormData((prev) => ({ ...prev, [key]: value }));

  const isRequestNewCodeButtonDisabled = isRequestingNewCode || resendTimerCode > 0;
  const isButtonDisabled = isRequestingNewCode || !uniqueCodeFormData.code || isSubmitting;

  const generateNewCode = (email: string) => {
    console.log("hit");
    setIsRequestingNewCode(true);
    setResendCodeTimer(defaultResetTimerValue);
  };

  return (
    <div className="relative w-screen h-screen overflow-hidden">
      <div className="absolute inset-0 z-0">
        <Image src={PlaneBackgroundPatternDark} className="w-full h-full object-cover" alt="background" />
      </div>
      <div className="relative z-10 w-screen h-screen overflow-hidden overflow-y-auto flex flex-col">
        <div className="container min-w-full px-10 lg:px-20 xl:px-36 flex-shrink-0 relative flex items-center justify-between pb-4 transition-all">
          <div className="flex items-center gap-x-2 py-10">
            <Link href={`/`} className="h-[30px] w-[133px]">
              <h1 className="text-3xl">
                Sync<span className="text-green-400">Turtle</span>
              </h1>
            </Link>
          </div>
          <div className="flex flex-col items-end sm:items-center sm:gap-2 sm:flex-row text-center text-sm font-medium">
            <p>
              New to Sync<span className="text-green-400">Turtle</span>?
            </p>
            <Link href={`/`} className="font-semibold hover:underline">
              Create an account
            </Link>
          </div>
        </div>
        <div className="flex-grow container mx-auto px-10 lg:max-w-md lg:px-5 py-10 lg:pt-28 transition-all">
          <div className="relative flex flex-col space-y-6">
            <div className="text-center space-y-1 py-4">
              <h3 className="flex gap-4 justify-center text-3xl font-bold">New Device Detected</h3>
              <p className="font-medium">
                For your security, a one-time magic code has been sent to your verified email address.
              </p>
            </div>
            <Form className="space-y-4">
              <Input type="hidden" name="email" defaultValue={uniqueCodeFormData.email} />
              <div className="w-full">
                <Input type="text" label="Magic Code" labelPlacement="outside" name="magicCode" isRequired />
                <div className="flex w-full justify-between px-1 text-xs pt-1">
                  <p className="flex items-center gap-1 font-medium text-green-700">
                    <CircleCheck height={12} width={12} />
                    Paste the code sent to your email
                  </p>
                  <button
                    type="button"
                    onClick={() => generateNewCode(uniqueCodeFormData.email)}
                    className={`${
                      isRequestingNewCode ? "text-primary-400" : "font-medium text-primary-300 hover:text-primary-200"
                    }`}
                    disabled={isRequestNewCodeButtonDisabled}
                  >
                    {resendTimerCode > 0 ? `resend in...${resendTimerCode}` : "Resend"}
                  </button>
                </div>
              </div>
              <Button type="submit" color="primary" className="w-full" radius="sm" isDisabled={isButtonDisabled}>
                {isSubmitting ? "Submitting..." : "Submit"}
              </Button>
            </Form>
          </div>
        </div>
      </div>
    </div>
  );
}
