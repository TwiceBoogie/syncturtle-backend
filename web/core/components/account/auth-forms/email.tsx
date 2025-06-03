import { Input } from "@/components/form-fields";
import { cn } from "@/helpers/common.helper";
import { checkEmailValidity } from "@/helpers/string.helper";
import { IEmailCheckData } from "@/types/authentication";
import { FC, FormEvent, useMemo, useRef, useState } from "react";

type TAuthEmailForm = {
  defaultEmail: string;
  onSubmit: (data: IEmailCheckData) => Promise<void>;
};

export const AuthEmailForm: FC<TAuthEmailForm> = (props) => {
  const { onSubmit, defaultEmail } = props;
  // states
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [email, setEmail] = useState(defaultEmail);

  const emailError = useMemo(
    () => (email && !checkEmailValidity(email) ? { email: "Email is invalid" } : undefined),
    [email]
  );

  const handleFormSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsSubmitting(true);
    const payload: IEmailCheckData = {
      email: email,
    };
    await onSubmit(payload);
    setIsSubmitting(false);
  };

  const isButtonDisabled = email.length === 0 || Boolean(emailError?.email) || isSubmitting;

  const [isFocused, setisFocused] = useState(true);
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <form onSubmit={handleFormSubmit} className="mt-5 space-y-4">
      <div
        className={cn(
          `relative flex items-center rounded-md border`,
          !isFocused && Boolean(emailError?.email) ? `border-red-500` : `border-onboarding-border-100`
        )}
      >
        <Input
          id="email"
          name="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="name@companey.com"
          className={`disable-autofill-style h-[46px] w-full placeholder:text-onboarding-text-400 autofill:bg-red-500 border-0 focus:bg-none active:bg-transparent`}
          autoComplete="on"
          autoFocus
          ref={inputRef}
        />
      </div>
    </form>
  );
};
