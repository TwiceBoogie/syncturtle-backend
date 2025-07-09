"use server";

import { API_AUTH_MAGIC_LOGIN, API_AUTH_MAGIC_REGISTER } from "@/constants";
import { EAuthModes } from "@/helpers/authentication.helper";
import { TApiErrorResponse, TMagicCodeResponse, TRegistrationResponse } from "@/types/authentication";
import { randomUUID } from "crypto";
import { cookies, headers } from "next/headers";
import { redirect } from "next/navigation";
import z, { ZodError } from "zod/v4";

const MagicCodeRequest = z.object({
  email: z.email(),
  magicCode: z.string().regex(/^[a-z]{4}-[a-z]{4}-[a-z]{4}$/, "Invalid magic code format"),
  mode: z.enum(EAuthModes),
  next_path: z.string().optional(),
});

type MagicCodeReqSchema = z.infer<typeof MagicCodeRequest>;

export async function sendMagicCode(formData: FormData) {
  const headersList = headers();
  const userAgent = (await headersList).get("user-agent") ?? "Unknown-UA";
  const ip = (await headersList).get("x-forwarded-for") ?? "Unknown-IP";
  const inputs = Object.fromEntries(formData.entries());
  const result = await MagicCodeRequest.parseAsync(inputs);
  const url = result.mode === EAuthModes.SIGN_IN ? API_AUTH_MAGIC_LOGIN : API_AUTH_MAGIC_REGISTER;
  const res = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "User-Agent": userAgent,
      "X-Forwarded-For": ip,
      credentials: "include",
    },
    body: JSON.stringify(result),
  });
  const data: TMagicCodeResponse = await res.json();
  console.log(data);
  if (!res.ok) {
    if (isApiErrorResponse(data)) {
      redirect(`/sign-up?error_code=${data.errorCode}`);
    } else if (res.status === 403) {
      redirect(`/sign-in?error_code=5061`);
    }
  }
  const successData = data as TRegistrationResponse;

  await setCookies(successData.deviceToken, successData.token);
  redirect("/home");
}

function transform(error: ZodError) {
  return error.issues.reduce((acc, issue) => {
    const path = issue.path.join(".");
    acc[path] = issue.message;
    return acc;
  }, {} as Record<string, string>);
}

function transformZodErrors(error: ZodError): TApiErrorResponse {
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

async function setCookies(deviceToken: string, jwt: string) {
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

function isApiErrorResponse(obj: unknown): obj is TApiErrorResponse {
  return typeof obj === "object" && obj !== null && "errorCode" in obj;
}
