import useTimer from "@/hooks/use-timer";
import { Button } from "@heroui/react";
import { CircleCheck } from "lucide-react";
import { FC } from "react";

type TResendButtonProps = {
  email: string;
  isRequestingNewCode: boolean;
  isRequestNewCodeButtonDisabled: boolean;
  generateNewCode: (email: string) => Promise<void>;
};

export const ResendButton: FC<TResendButtonProps> = (props) => {
  const { email, isRequestingNewCode, isRequestNewCodeButtonDisabled, generateNewCode } = props;
  const { timer: resendTimerCode, setTimer: setResendCodeTimer } = useTimer(0);

  //   const isRequestNewCodeButtonDisabled = isRequestingNewCode || resendTimerCode > 0;
  return (
    <div className="flex w-full justify-between px-1 text-xs pt-1">
      <p className="flex items-center gap-1 font-medium text-green-700">
        <CircleCheck height={12} width={12} />
        Paste the code sent to your email
      </p>
      <button
        type="button"
        onClick={() => generateNewCode(email)}
        className={`${
          isRequestingNewCode ? "text-primary-400" : "font-medium text-primary-300 hover:text-primary-200"
        }`}
        disabled={isRequestNewCodeButtonDisabled}
      >
        {resendTimerCode > 0 ? `resend in...${resendTimerCode}` : "Resend"}
      </button>
    </div>
  );
};
