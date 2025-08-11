import { FC, useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
// components
import {
  AuthHeader,
  AuthBanner,
  AuthEmailForm,
  TermsAndConditions,
  AuthUniqueCodeForm,
  AuthPassword,
} from "@/components/account";
// helpers
import {
  authErrorHandler,
  EAuthenticationErrorCodes,
  EAuthMagicMode,
  EAuthModes,
  EAuthSteps,
  EErrorAlertType,
  TAuthErrorInfo,
} from "@/helpers/authentication.helper";
// server actions
import { emailCheck } from "@/actions/auth-email-check";
import { generateUniqueCode } from "@/actions/auth-generate-magic-code";
// types
import { IEmailCheckData } from "@/types/authentication";
import { OAuthOptions } from "../oauth";

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

  useEffect(() => {
    if (error_code && authMode) {
      const errorHandler = authErrorHandler(error_code?.toString() as EAuthenticationErrorCodes);
      if (errorHandler) {
      }
      setErrorInfo(errorHandler);
    }
  }, [error_code, authMode]);

  const handleEmailVerification = async (data: IEmailCheckData) => {
    setEmail(data.email);
    setErrorInfo(undefined);
    const res = await emailCheck(data);
    if (res.ok) {
      const response = res.data;

      if (response.existing) {
        if (currentAuthMode === EAuthModes.SIGN_UP) setAuthMode(EAuthModes.SIGN_IN);
        if (response.status === "MAGIC_CODE") {
          setAuthStep(EAuthSteps.UNIQUE_CODE);
          await generateEmailUniqueCode(data.email);
        } else if (response.status === "CREDENTIAL") {
          setAuthStep(EAuthSteps.PASSWORD);
        }
      } else {
        if (currentAuthMode === EAuthModes.SIGN_IN) setAuthMode(EAuthModes.SIGN_UP);
        if (response.status === "MAGIC_CODE") {
          setAuthStep(EAuthSteps.UNIQUE_CODE);
          await generateEmailUniqueCode(data.email);
        }
      }
      setIsExistingEmail(response.existing);
    } else {
      const error = res.error;
      const errorHandler = authErrorHandler(error.error_code.toString() as any, data.email);
      if (errorHandler) setErrorInfo(errorHandler);
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
    const payload = { email: email, mode: EAuthMagicMode.MAGIC_CODE };
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
