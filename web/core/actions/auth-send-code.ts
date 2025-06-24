"use server";

import { API_AUTH_MAGIC_LOGIN, API_AUTH_MAGIC_REGISTER } from "@/constants";
import { EAuthModes } from "@/helpers/authentication.helper";
import { IRegistrationResponse } from "@/types/authentication";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import z, { ZodError } from "zod/v4";

const MagicCodeRequest = z.object({
  email: z.email(),
  magicCode: z.string().regex(/^[a-z]{4}-[a-z]{4}-[a-z]{4}$/, "Invalid magic code format"),
});

type MagicCodeReqSchema = z.infer<typeof MagicCodeRequest>;

export async function sendMagicCode(prevState: any, formData: FormData) {
  try {
    const inputs = Object.fromEntries(formData);
    const payload = {
      email: inputs.email,
      magicCode: inputs.magicCode,
    };
    const result = await MagicCodeRequest.parseAsync(payload);
    const url = inputs.mode === EAuthModes.SIGN_IN ? API_AUTH_MAGIC_LOGIN : API_AUTH_MAGIC_REGISTER;
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(result),
    });
    const data: IRegistrationResponse = await res.json();

    if (res.ok) {
      await setCookies(data.deviceToken, data.token);
    }
    return { ok: true, errors: {} };
  } catch (error) {
    if (error instanceof ZodError) {
      return { ok: false, errors: transform(error) };
    }
    return { ok: false, errors: { server: "Something went wrong" } };
  }
}

function transform(error: ZodError) {
  return error.issues.reduce((acc, issue) => {
    const path = issue.path.join(".");
    acc[path] = issue.message;
    return acc;
  }, {} as Record<string, string>);
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
}
