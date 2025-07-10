"use server";

import { isApiErrorResponse } from "@/helpers/server.helper";
import { IEmailCheckResponse, TEmailCheckResponse } from "@/types/authentication";
import { redirect } from "next/navigation";
import { z } from "zod/v4";

const Email = z.object({
  email: z.email(),
});

type EmailSchema = z.infer<typeof Email>;

export async function emailCheck(payload: EmailSchema) {
  const result = await Email.safeParseAsync(payload);
  if (result.success) {
    const res = await fetch("http://localhost:8000/ui/v1/auth/check-email", {
      method: "POST",
      body: JSON.stringify(result.data),
      headers: {
        "Content-Type": "application/json",
      },
    });

    const data: TEmailCheckResponse = await res.json();

    if (!res.ok) {
      if (isApiErrorResponse(data)) {
        redirect(`/accounts/new-device?error_code=${data.errorCode}&email=${result.data.email}`);
      }
    }
    const successData = data as IEmailCheckResponse;
    return { ok: true, data: successData };
  } else {
    return {
      ok: false,
      data: {
        email: "",
        status: "CREDENTIAL",
        existing: false,
        passwordAutoSet: false,
      } as IEmailCheckResponse,
    };
  }
}
