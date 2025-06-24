import { FC, FormEvent, useActionState, useEffect, useState } from "react";
import { AuthService } from "@/services/auth.service";
import { EAuthModes } from "@/helpers/authentication.helper";
import useTimer from "@/hooks/use-timer";
import { Form } from "@heroui/form";
import { Input } from "@heroui/input";
import { Button } from "@heroui/button";
import { CircleCheck } from "lucide-react";
import { sendMagicCode } from "@/actions/auth-send-code";
import { useRouter } from "next/navigation";

// services
const authService = new AuthService();

type TAuthUniqueCodeForm = {
  mode: EAuthModes;
  email: string;
  isExistingEmail: boolean;
  handleEmailClear: () => void;
  generateEmailUniqueCode: (email: string) => Promise<void>;
  nextPath: string | undefined;
};

type TUniqueCodeFormValues = {
  email: string;
  code: string;
};

const defaultValues: TUniqueCodeFormValues = {
  email: "",
  code: "",
};

export const AuthUniqueCode: FC<TAuthUniqueCodeForm> = (props) => {
  const { mode, email, handleEmailClear, generateEmailUniqueCode, isExistingEmail, nextPath } = props;
  const router = useRouter();
  // derived values
  const defaultResetTimerValue = 5;
  // states
  const [state, action, isPending] = useActionState(sendMagicCode, null);
  const [errors, setErrors] = useState<Record<string, string>>();
  const [uniqueCodeFormData, setUniqueCodeFormData] = useState<TUniqueCodeFormValues>({ ...defaultValues, email });
  const [isRequestingNewCode, setIsRequestingNewCode] = useState(false);
  // timer
  const { timer: resendTimerCode, setTimer: setResendCodeTimer } = useTimer(0);

  const handleFormChange = (key: keyof TUniqueCodeFormValues, value: string) =>
    setUniqueCodeFormData((prev) => ({ ...prev, [key]: value }));

  const generateNewCode = async (email: string) => {
    try {
      setIsRequestingNewCode(true);
      await generateEmailUniqueCode(email);
      setResendCodeTimer(defaultResetTimerValue);
      handleFormChange("code", "");
      setIsRequestingNewCode(false);
    } catch {
      setResendCodeTimer(0);
      console.error("Error while requesting new code");
      setIsRequestingNewCode(false);
    }
  };

  useEffect(() => {
    if (state) {
      if (state.ok) {
        router.push("/home");
      } else {
        setErrors(state.errors);
      }
    }
  }, [state]);

  const isRequestNewCodeButtonDisabled = isRequestingNewCode || resendTimerCode > 0;
  const isButtonDisabled = isRequestingNewCode || !uniqueCodeFormData.code || isPending;

  return (
    <Form action={action} validationErrors={errors} className="mt-5 space-y-4">
      <Input type="hidden" name="mode" defaultValue={mode} />
      <Input
        label="Email"
        labelPlacement="outside"
        name="email"
        type="email"
        defaultValue={uniqueCodeFormData.email}
        isClearable
        readOnly
        onClear={handleEmailClear}
        radius="sm"
      />
      <div className="w-full">
        <Input
          label="Unique Code"
          labelPlacement="outside"
          name="magicCode"
          value={uniqueCodeFormData.code}
          onValueChange={(e) => handleFormChange("code", e)}
          placeholder="asdf-asdf-asdf"
          radius="sm"
        />
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
        {isPending ? "Sending" : "Send"}
      </Button>
    </Form>
  );
};
