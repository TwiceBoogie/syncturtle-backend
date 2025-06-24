// import { Input } from "@/components/form-fields";
import { cn } from "@/helpers/common.helper";
import { checkEmailValidity } from "@/helpers/string.helper";
import { IEmailCheckData } from "@/types/authentication";
import { Button, Form, Input } from "@heroui/react";
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

  return (
    <Form onSubmit={handleFormSubmit} className="mt-5 space-y-4">
      <Input
        label="Email"
        labelPlacement="outside"
        required
        isClearable
        onValueChange={setEmail}
        name="email"
        type="email"
        placeholder="name@companey.com"
        radius="sm"
      />

      <Button
        type="submit"
        color="primary"
        className="w-full"
        radius="sm"
        isDisabled={isButtonDisabled}
        isLoading={isSubmitting}
      >
        {isSubmitting ? "Loading" : "Continue"}
      </Button>
    </Form>
  );
};
