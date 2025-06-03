import { EAuthModes, EAuthSteps, EErrorAlertType, TAuthErrorInfo } from "@/helpers/authentication.helper";
import { AuthService } from "@/services/auth.service";
import { useSearchParams } from "next/navigation";
import { FC, useEffect, useState } from "react";
import { AuthHeader } from "./auth-header";
import { AuthBanner } from "./auth-banner";
import { AuthEmailForm } from "./email";
import { IEmailCheckData } from "@/types/authentication";

const authService = new AuthService();

type TAuthRoot = {
  authMode: EAuthModes;
};

export const AuthRoot: FC<TAuthRoot> = (props) => {
  const searchParams = useSearchParams();
  // query params
  const emailParam = searchParams.get("email");
  const error_code = searchParams.get("error_code");
  const nexPath = searchParams.get("next_path");
  // props
  const { authMode: currentAuthStep } = props;
  // states
  const [authMode, setAuthMode] = useState<EAuthModes | undefined>(undefined);
  const [authStep, setAuthStep] = useState<EAuthSteps>(EAuthSteps.EMAIL);
  const [email, setEmail] = useState(emailParam ? emailParam.toString() : "");
  const [errorInfo, setErrorInfo] = useState<TAuthErrorInfo | undefined>(undefined);
  const [isExistingEmail, setIsExistingEmail] = useState(false);

  useEffect(() => {
    if (!authMode && currentAuthStep) setAuthMode(currentAuthStep);
  }, [currentAuthStep, authMode]);

  const handleEmailVerification = async (data: IEmailCheckData) => {
    setEmail(data.email);
    setErrorInfo(undefined);
  };

  if (!authMode) return <></>;
  return (
    <div className="relative flex flex-col space-y-6">
      <AuthHeader authMode={authMode} currentAuthStep={authStep}>
        {errorInfo && errorInfo?.type === EErrorAlertType.BANNER_ALERT && (
          <AuthBanner bannerData={errorInfo} handleBannerData={(value) => setErrorInfo(value)} />
        )}
        {authStep === EAuthSteps.EMAIL && <AuthEmailForm defaultEmail={email} onSubmit={handleEmailVerification} />}
      </AuthHeader>
    </div>
  );
};
