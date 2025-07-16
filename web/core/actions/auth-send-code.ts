"use server";

import { API_AUTH_MAGIC_LOGIN, API_AUTH_MAGIC_REGISTER } from "@/constants";
import { EAuthModes } from "@/helpers/authentication.helper";
import { isApiErrorResponse, setCookies } from "@/helpers/server.helper";
import { TMagicCodeResponse, TRegistrationResponse } from "@/types/authentication";
import { headers } from "next/headers";
import { redirect } from "next/navigation";
import z from "zod/v4";

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
      if (data.errorCode === 5061) {
        redirect(`/accounts/new-device?error_code=${data.errorCode}&email=${encodeURIComponent(result.email)}`);
      }
      redirect(`/sign-up?error_code=${data.errorCode}`);
    } else if (res.status === 403) {
      redirect(`/sign-in?error_code=5061`);
    }
  }
  const successData = data as TRegistrationResponse;

  await setCookies(successData.deviceToken, successData.token);
  redirect("/home");
}
