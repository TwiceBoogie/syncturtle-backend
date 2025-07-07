import { FC, useActionState, useEffect, useMemo, useState } from "react";
import { Form } from "@heroui/form";
import { Input } from "@heroui/input";
import { EAuthModes, EAuthSteps } from "@/helpers/authentication.helper";
import { LockKeyhole, LockKeyholeOpen } from "lucide-react";
import { Button } from "@heroui/react";
import { login } from "@/actions/auth-login";

type Props = {
  email: string;
  mode: EAuthModes;
  handleEmailClear: () => void;
  handleAuthStep: (step: EAuthSteps) => void;
};

type TPasswordFormValues = {
  email: string;
  password: string;
  confirm_password?: string;
};

const defaultValues: TPasswordFormValues = {
  email: "",
  password: "",
};

export const AuthPassword: FC<Props> = (props) => {
  const { email, mode, handleEmailClear, handleAuthStep } = props;
  const [passwordFormData, setPasswordFormData] = useState<TPasswordFormValues>({ ...defaultValues, email });
  const [showPassword, setShowPassword] = useState({
    password: false,
    retypePassword: false,
  });
  const [state, formAction, isPending] = useActionState(login, null);
  const [errors, setErrors] = useState<Record<string, string>>();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isPasswordInputFocused, setIsPasswordInputFocused] = useState(false);
  const [isRetryPasswordInputFocused, setIsRetryPasswordInputFocused] = useState(false);
  const [isBannerMessage, setBannerMessage] = useState(false);

  const handleShowPassword = (key: keyof typeof showPassword) =>
    setShowPassword((prev) => ({ ...prev, [key]: !prev[key] }));

  const handleFormChange = (key: keyof TPasswordFormValues, value: string) =>
    setPasswordFormData((prev) => ({ ...prev, [key]: value }));

  const isButtonDisabled = useMemo(
    () =>
      !isSubmitting &&
      !!passwordFormData.password &&
      (mode === EAuthModes.SIGN_UP ? passwordFormData.password === passwordFormData.confirm_password : true)
        ? false
        : true,
    [isSubmitting, mode, passwordFormData.confirm_password, passwordFormData.password]
  );

  useEffect(() => {
    if (state) {
      if (state.ok) {
        console.log("something");
      } else {
        setErrors(state.errors);
      }
    }
  });

  return (
    <Form action={formAction} validationErrors={errors} className="mt-5 space-y-4">
      <Input type="hidden" name="mode" defaultValue={mode} />
      <Input
        type="email"
        name="email"
        label="Email"
        labelPlacement="outside"
        defaultValue={passwordFormData.email}
        isReadOnly
        isClearable
        onClear={handleEmailClear}
        radius="sm"
      />
      <Input
        type={showPassword.password ? "text" : "password"}
        name="password"
        label="Password"
        labelPlacement="outside"
        placeholder="Enter password"
        radius="sm"
        onValueChange={(e) => handleFormChange("password", e)}
        value={passwordFormData.password}
        endContent={
          <button
            aria-label="toggle password visibility"
            className="focus:outline-none"
            type="button"
            onClick={() => handleShowPassword("password")}
          >
            {showPassword.password ? (
              <LockKeyhole className="h-4 w-4 pointer-events-none" />
            ) : (
              <LockKeyholeOpen className="h-4 w-4 pointer-events-none" />
            )}
          </button>
        }
      />
      {mode === EAuthModes.SIGN_UP && (
        <Input name="confirmPassword" label="Confirm Password" labelPlacement="outside" radius="sm" />
      )}
      <Button type="submit" className="w-full" color="primary" radius="sm" isDisabled={isButtonDisabled}>
        {isSubmitting ? "Submitting" : "Submit"}
      </Button>
    </Form>
  );
};
