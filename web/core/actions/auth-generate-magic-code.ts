"use server";

import z, { ZodError } from "zod/v4";

const Email = z.object({
  email: z.email(),
});

type EmailSchema = z.infer<typeof Email>;

export async function generateUniqueCode(payload: EmailSchema) {
  try {
    const result = await Email.parseAsync(payload);
    const res = await fetch("http://localhost:8000/ui/v1/auth/generate-magic-code", {
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
