import {
  authErrorHandler,
  EAuthModes,
  EAuthSteps,
  EErrorAlertType,
  TAuthErrorInfo,
} from "@/helpers/authentication.helper";
import { AuthService } from "@/services/auth.service";
import { useRouter, useSearchParams } from "next/navigation";
import { FC, useEffect, useState } from "react";
import { AuthHeader } from "./auth-header";
import { AuthBanner } from "./auth-banner";
import { AuthEmailForm } from "./email";
import { IEmailCheckData } from "@/types/authentication";
import { OAuthOptions } from "../oauth";
import { TermsAndConditions } from "../terms-and-conditions";
import { AuthUniqueCodeForm } from "./unique-code";
import { emailCheck } from "@/actions/auth-email-check";
import { generateUniqueCode } from "@/actions/auth-generate-magic-code";
import { AuthPassword } from "./password";

type TAuthRoot = {
  authMode: EAuthModes;
};

export const AuthRoot: FC<TAuthRoot> = (props) => {
  const router = useRouter();
  const searchParams = useSearchParams();
  // query params
  const emailParam = searchParams.get("email");
  const error_code = searchParams.get("error_code");
  const nexPath = searchParams.get("next_path");
  // props
  const { authMode: currentAuthMode } = props;
  // states
  const [authMode, setAuthMode] = useState<EAuthModes | undefined>(undefined);
  const [authStep, setAuthStep] = useState<EAuthSteps>(EAuthSteps.EMAIL);
  const [email, setEmail] = useState(emailParam ? emailParam.toString() : "");
  const [errorInfo, setErrorInfo] = useState<TAuthErrorInfo | undefined>(undefined);
  const [isExistingEmail, setIsExistingEmail] = useState(false);

  useEffect(() => {
    if (!authMode && currentAuthMode) setAuthMode(currentAuthMode);
  }, [currentAuthMode, authMode]);

  const handleEmailVerification = async (data: IEmailCheckData) => {
    setEmail(data.email);
    setErrorInfo(undefined);
    const res = await emailCheck(data);
    if (res.ok) {
      const dataFromServer = res.data;

      if (dataFromServer.existing) {
        if (currentAuthMode === EAuthModes.SIGN_UP) setAuthMode(EAuthModes.SIGN_IN);
        if (dataFromServer.status === "MAGIC_CODE") {
          setAuthStep(EAuthSteps.UNIQUE_CODE);
          await generateEmailUniqueCode(data.email);
        } else if (dataFromServer.status === "CREDENTIAL") {
          setAuthStep(EAuthSteps.PASSWORD);
        }
      } else {
        if (currentAuthMode === EAuthModes.SIGN_IN) setAuthMode(EAuthModes.SIGN_UP);
        if (dataFromServer.status === "MAGIC_CODE") {
          setAuthStep(EAuthSteps.UNIQUE_CODE);
          await generateEmailUniqueCode(data.email);
        }
      }
      setIsExistingEmail(dataFromServer.existing);
    }
  };

  const handleEmailClear = () => {
    setAuthMode(currentAuthMode);
    setErrorInfo(undefined);
    setEmail("");
    setAuthStep(EAuthSteps.EMAIL);
    router.push(currentAuthMode === EAuthModes.SIGN_IN ? `/` : `/sign-up`);
  };

  const generateEmailUniqueCode = async (email: string) => {
    const payload = { email: email };
    const res = await generateUniqueCode(payload);
    if (!res.ok) {
      console.log("inside generateEmailUniqueCode: ", res);
      const errorHandler = authErrorHandler(res.data?.errorCode.toString());
      if (errorHandler?.type) setErrorInfo(errorHandler);
    }
    return;
  };

  if (!authMode) return <></>;
  return (
    <div className="relative flex flex-col space-y-6">
      <AuthHeader authMode={authMode} currentAuthStep={authStep}>
        {errorInfo && errorInfo?.type === EErrorAlertType.BANNER_ALERT && (
          <AuthBanner bannerData={errorInfo} handleBannerData={(value) => setErrorInfo(value)} />
        )}
        {authStep === EAuthSteps.EMAIL && <AuthEmailForm defaultEmail={email} onSubmit={handleEmailVerification} />}
        {authStep === EAuthSteps.UNIQUE_CODE && (
          <AuthUniqueCodeForm
            mode={authMode}
            email={email}
            isExistingEmail={isExistingEmail}
            handleEmailClear={handleEmailClear}
            generateEmailUniqueCode={generateEmailUniqueCode}
            nextPath={nexPath || undefined}
          />
        )}
        {authStep === EAuthSteps.PASSWORD && (
          <AuthPassword
            email={email}
            mode={authMode}
            handleEmailClear={handleEmailClear}
            handleAuthStep={(step: EAuthSteps) => {
              if (step === EAuthSteps.UNIQUE_CODE) generateEmailUniqueCode(email);
              setAuthStep(step);
            }}
          />
        )}
        <OAuthOptions isSignUp={authMode === EAuthModes.SIGN_UP} />
        <TermsAndConditions isSignUp={authMode === EAuthModes.SIGN_UP} />
      </AuthHeader>
    </div>
  );
};
