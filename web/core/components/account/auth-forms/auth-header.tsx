import { EAuthModes, EAuthSteps } from "@/helpers/authentication.helper";
import { FC, ReactNode } from "react";

type TAuthHeader = {
  authMode: EAuthModes;
  currentAuthStep: EAuthSteps;
  children: ReactNode;
};

const Titles = {
  [EAuthModes.SIGN_IN]: {
    [EAuthSteps.EMAIL]: {
      header: "Log in or sign up",
      subHeader: "",
    },
    [EAuthSteps.PASSWORD]: {
      header: "Log in or sign up",
      subHeader: "Use your email-password combination to log in",
    },
    [EAuthSteps.UNIQUE_CODE]: {
      header: "Log in or sign up",
      subHeader: "Log in using a unique code sent to the email address above",
    },
  },
  [EAuthModes.SIGN_UP]: {
    [EAuthSteps.EMAIL]: {
      header: "Sign up",
      subHeader: "",
    },
    [EAuthSteps.PASSWORD]: {
      header: "Sign up",
      subHeader: "Sign up using an email-password combination",
    },
    [EAuthSteps.UNIQUE_CODE]: {
      header: "Sign up",
      subHeader: "Sign up using a unique code sent to the email address above.",
    },
  },
};

export const AuthHeader: FC<TAuthHeader> = (props) => {
  const { authMode, currentAuthStep, children } = props;
  const { header, subHeader } = Titles[authMode][currentAuthStep];

  return (
    <>
      <div className="space-y-1 text-center">
        <h3 className="text-3xl font-bold">{header}</h3>
        {subHeader && <p className="font-medium">{subHeader}</p>}
      </div>
      {children}
    </>
  );
};
