"use server";

import { API_AUTH_LOGIN, API_AUTH_REGISTRATION_CHECK } from "@/constants";
import { EAuthModes } from "@/helpers/authentication.helper";
import { headers } from "next/headers";
import { z, ZodError } from "zod/v4";

const LoginFormRequest = z.object({
  email: z.email(),
  password: z
    .string()
    .min(8)
    .check((ctx) => {
      const password = ctx.value;

      const containsUppercase = (ch: string) => /[A-Z]/.test(ch);
      const containsLowercase = (ch: string) => /[a-z]/.test(ch);
      const containsSpecialChar = (ch: string) => /[`!@#$%^&*()_\-+=\[\]{};':"\\|,.<>\/?~ ]/.test(ch);

      let countOfUpperCase = 0;
      let countOfLowerCase = 0;
      let countOfNumbers = 0;
      let countOfSpecialChar = 0;

      for (let i = 0; i < password.length; i++) {
        const ch = password.charAt(i);
        if (!isNaN(+ch)) countOfNumbers++;
        else if (containsUppercase(ch)) countOfUpperCase++;
        else if (containsLowercase(ch)) countOfLowerCase++;
        else if (containsSpecialChar(ch)) countOfSpecialChar++;
      }

      if (countOfLowerCase < 1) {
        ctx.issues.push({
          code: "custom",
          message: "Password must contain at least one lowercase letter.",
          input: ctx.value,
        });
      }

      if (countOfUpperCase < 1) {
        ctx.issues.push({
          code: "custom",
          message: "Password must contain at least one uppercase letter.",
          input: ctx.value,
        });
      }

      if (countOfNumbers < 1) {
        ctx.issues.push({
          code: "custom",
          message: "Password must contain at least one number.",
          input: ctx.value,
        });
      }

      if (countOfSpecialChar < 1) {
        ctx.issues.push({
          code: "custom",
          message: "Password must contain at least one special character.",
          input: ctx.value,
        });
      }
    }),
});

const RegisterFormRequest = z
  .object({
    email: z.email(),
    password: z
      .string()
      .min(8)
      .check((ctx) => {
        const password = ctx.value;

        const containsUppercase = (ch: string) => /[A-Z]/.test(ch);
        const containsLowercase = (ch: string) => /[a-z]/.test(ch);
        const containsSpecialChar = (ch: string) => /[`!@#$%^&*()_\-+=\[\]{};':"\\|,.<>\/?~ ]/.test(ch);

        let countOfUpperCase = 0;
        let countOfLowerCase = 0;
        let countOfNumbers = 0;
        let countOfSpecialChar = 0;

        for (let i = 0; i < password.length; i++) {
          const ch = password.charAt(i);
          if (!isNaN(+ch)) countOfNumbers++;
          else if (containsUppercase(ch)) countOfUpperCase++;
          else if (containsLowercase(ch)) countOfLowerCase++;
          else if (containsSpecialChar(ch)) countOfSpecialChar++;
        }

        if (countOfLowerCase < 1) {
          ctx.issues.push({
            code: "custom",
            message: "Password must contain at least one lowercase letter.",
            input: ctx.value,
          });
        }

        if (countOfUpperCase < 1) {
          ctx.issues.push({
            code: "custom",
            message: "Password must contain at least one uppercase letter.",
            input: ctx.value,
          });
        }

        if (countOfNumbers < 1) {
          ctx.issues.push({
            code: "custom",
            message: "Password must contain at least one number.",
            input: ctx.value,
          });
        }

        if (countOfSpecialChar < 1) {
          ctx.issues.push({
            code: "custom",
            message: "Password must contain at least one special character.",
            input: ctx.value,
          });
        }
      }),
    confirmPassword: z.string(),
  })
  .check((ctx) => {
    const { password, confirmPassword } = ctx.value;

    if (password !== confirmPassword) {
      ctx.issues.push({
        code: "custom",
        message: "Passwords do not match",
        path: ["confirmPassword"],
        input: ctx.value,
      });
    }
  });

export async function login(prevState: any, formData: FormData) {
  try {
    const headersList = headers();
    const userAgent = (await headersList).get("user-agent") ?? "Unknown-UA";
    const ip = (await headersList).get("x-forwarded-for") ?? "Unknown-IP";
    const inputs = Object.fromEntries(formData);
    const payload =
      inputs.mode === EAuthModes.SIGN_IN
        ? {
            email: inputs.email,
            password: inputs.password,
          }
        : {
            email: inputs.email,
            password: inputs.password,
            confirmPassword: inputs.confirmPassword,
          };
    let url = inputs.mode === EAuthModes.SIGN_IN ? API_AUTH_LOGIN : API_AUTH_REGISTRATION_CHECK;
    const result =
      inputs.mode === EAuthModes.SIGN_IN
        ? await LoginFormRequest.parseAsync(payload)
        : await RegisterFormRequest.parseAsync(payload);
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "User-Agent": userAgent,
        "X-Forwarded-For": ip,
        credentails: "include",
      },
      body: JSON.stringify(result),
    });
    const data = await res.json();
    console.log(data);
    return { ok: true, errors: {} };
  } catch (error) {
    console.log(error);
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
