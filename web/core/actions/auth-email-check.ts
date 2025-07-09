"use server";

import { IEmailCheckResponse } from "@/types/authentication";
import { z } from "zod/v4";

const Email = z.object({
  email: z.email(),
});

type EmailSchema = z.infer<typeof Email>;

export async function emailCheck(payload: EmailSchema) {
  try {
    // parse form data
    const result = await Email.parseAsync(payload);
    // fetch
    const res = await fetch("http://localhost:8000/ui/v1/auth/check-email", {
      method: "POST",
      body: JSON.stringify(result),
      headers: {
        "Content-Type": "application/json",
      },
    });
    const data: IEmailCheckResponse = await res.json();
    return { ok: true, data };
  } catch (error) {
    console.log("inside error", error);
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
