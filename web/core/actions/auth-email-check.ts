"use server";

import { EAuthenticationErrorCodes, errorCodeMessages } from "@/helpers/authentication.helper";
import { API_BASE_URL } from "@/helpers/common.helper";
import { isApiErrorResponse } from "@/helpers/server.helper";
import { HttpError } from "@/lib/errors/api-error";
import {
  IAuthErrorResponse,
  IEmailCheckResponse,
  TEmailCheckResponse,
  TEmailCheckResult,
} from "@/types/authentication";
import { Result } from "@/types/common";
import { redirect } from "next/navigation";
import { z } from "zod/v4";

const Email = z.object({
  email: z.email(),
});

type EmailSchema = z.infer<typeof Email>;

export async function emailCheck(payload: EmailSchema): Promise<Result<IEmailCheckResponse, IAuthErrorResponse>> {
  const result = await Email.safeParseAsync(payload);
  if (!result.success) {
    return {
      ok: false,
      error: {
        error_code: parseInt(EAuthenticationErrorCodes.INVALID_EMAIL, 10),
        error_message: "Something went wrong. Please try again.",
      },
    };
  }

  try {
    const res = await fetch(`${API_BASE_URL}/auth/email-check/`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(result.data),
    });

    const data = await res.json();

    if (!res.ok && isApiErrorResponse(data)) {
      return { ok: false, error: data };
    }

    return {
      ok: true,
      data: data as IEmailCheckResponse,
    };
  } catch (error) {
    return {
      ok: false,
      error: {
        error_code: 5000,
        error_message: "Unexpected server error",
      },
    };
  }
}
