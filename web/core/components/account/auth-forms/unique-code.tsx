import { FC, useEffect, useRef, useState } from "react";
import { EAuthModes } from "@/helpers/authentication.helper";
import useTimer from "@/hooks/use-timer";
import { Form } from "@heroui/form";
import { Input } from "@heroui/input";
import { Button } from "@heroui/button";
import { CircleCheck } from "lucide-react";
import { sendMagicCode } from "@/actions/auth-send-code";
import { AuthService } from "@/services/auth.service";
import { API_BASE_URL } from "@/helpers/common.helper";

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

const authService = new AuthService();

export const AuthUniqueCodeForm: FC<TAuthUniqueCodeForm> = (props) => {
  const { mode, email, handleEmailClear, generateEmailUniqueCode, isExistingEmail, nextPath } = props;
  // derived values
  const defaultResetTimerValue = 5;
  // ref
  const formRef = useRef<HTMLFormElement>(null);
  // states
  const [uniqueCodeFormData, setUniqueCodeFormData] = useState<TUniqueCodeFormValues>({ ...defaultValues, email });
  const [isRequestingNewCode, setIsRequestingNewCode] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [csrfPromise, setCsrfPromise] = useState<Promise<{ csrfToken: string }> | undefined>(undefined);
  // timer
  const { timer: resendTimerCode, setTimer: setResendCodeTimer } = useTimer(0);

  useEffect(() => {
    if (csrfPromise === undefined) {
      const promise = authService.requestCsrfToken();
      setCsrfPromise(promise);
    }
  }, [csrfPromise]);

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

  const isRequestNewCodeButtonDisabled = isRequestingNewCode || resendTimerCode > 0;
  const isButtonDisabled = isRequestingNewCode || !uniqueCodeFormData.code || isSubmitting;

  const handleCsrfToken = async () => {
    if (!formRef || !formRef.current) return;
    const token = await csrfPromise;
    if (!token?.csrfToken) return;
    const csrfElement = formRef.current.querySelector("input[name=csrftoken]");
    csrfElement?.setAttribute("value", token.csrfToken);
  };

  return (
    <Form
      action={
        `${API_BASE_URL}/ui/v1/auth/${mode === EAuthModes.SIGN_IN ? "magic-sign-in" : "magic-sign-up"}/` +
        (nextPath ? `?next_path=${encodeURIComponent(nextPath)}` : "")
      }
      onSubmit={async (event) => {
        event.preventDefault();
        await handleCsrfToken();
        setIsSubmitting(true);
        if (formRef.current) formRef.current.submit();
      }}
      className="mt-5 space-y-4"
      ref={formRef}
    >
      <input type="hidden" name="csrftoken" />
      {nextPath && <input type="hidden" name="next_path" value={nextPath} />}
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
        {isSubmitting ? "Sending..." : "Send"}
      </Button>
    </Form>
  );
};
