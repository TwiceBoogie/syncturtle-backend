import { GoogleOAuthButton } from "./google-button";

type TOAuthOptionProps = {
  isSignUp?: boolean;
};

export const OAuthOptions: React.FC<TOAuthOptionProps> = (props) => {
  const config = true;
  return (
    <>
      <div className="mt-4 flex items-center">
        <hr className="w-full border-onboarding-border-100" />
        <p className="mx-3 flex-shrink-0 text-center text-sm text-onboarding-text-400">or</p>
        <hr className="w-full border-onboarding-border-100" />
      </div>
      <div className={`mt-7 grid gap-4 overflow-hidden`}>
        {config && (
          <div className="flex">
            <GoogleOAuthButton text="Continue with Google" />
          </div>
        )}
      </div>
    </>
  );
};
