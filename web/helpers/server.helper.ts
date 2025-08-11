import { IAuthErrorResponse } from "@/types/authentication";
import { randomUUID } from "crypto";
import { cookies } from "next/headers";
import { ZodError } from "zod/v4";

export function isApiErrorResponse(obj: unknown): obj is IAuthErrorResponse {
  return typeof obj === "object" && obj !== null && "error_code" in obj;
}

export function transformZodErrors(error: ZodError): IAuthErrorResponse {
  return {
    errorCode: 4001,
    errorMessage: "VALIDATION_FAILED",
    payload: error.issues.reduce((acc, issue) => {
      const key = issue.path.join(".");
      acc[key] = issue.message;
      return acc;
    }, {} as Record<string, string>),
  };
}

export async function setCookies(deviceToken: string, jwt: string) {
  const cookieStore = await cookies();

  // set short-lived JWT (4hours);
  cookieStore.set("deviceToken", deviceToken, {
    httpOnly: true,
    path: "/",
    sameSite: "strict",
    maxAge: 60 * 60 * 4, // hours
  });

  cookieStore.set("token", jwt, {
    httpOnly: true,
    path: "/",
    sameSite: "strict",
    maxAge: 60 * 60 * 24 * 90, // 90 days
  });
  // csrf token (4 hours)
  const csrfToken = randomUUID();
  cookieStore.set("XSRF-TOKEN", csrfToken, {
    httpOnly: false,
    path: "/",
    sameSite: "strict",
    maxAge: 60 * 60 * 4,
  });
}
