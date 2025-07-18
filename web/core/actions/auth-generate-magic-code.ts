"use server";

import { EAuthMagicMode } from "@/helpers/authentication.helper";
import { API_BASE_URL } from "@/helpers/common.helper";
import z, { ZodError } from "zod/v4";

const MagicCodeResendPayload = z.object({
  email: z.email(),
  mode: z.enum([EAuthMagicMode.MAGIC_CODE, EAuthMagicMode.MAGIC_DEVICE_CODE]),
});

export type TMagicCodePayload = z.infer<typeof MagicCodeResendPayload>;

export async function generateUniqueCode(payload: TMagicCodePayload) {
  try {
    const result = await MagicCodeResendPayload.parseAsync(payload);
    const endpoint = result.mode === EAuthMagicMode.MAGIC_CODE ? "generate-magic-code" : "generate-magic-code-device";
    const res = await fetch(`${API_BASE_URL}/ui/v1/auth/${endpoint}`, {
      method: "POST",
      body: JSON.stringify(result),
      headers: {
        "Content-Type": "application/json",
      },
    });
    const data = await res.json();

    if (!res.ok) {
      return { ok: false, data: data, errors: {} };
    }
    return { ok: true, errors: {} };
  } catch (error) {
    console.log("inside catch error: ", error);
    if (error instanceof ZodError) {
      console.log(z.treeifyError(error));
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
