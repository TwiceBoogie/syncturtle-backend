"use server";

import z, { ZodError } from "zod/v4";

const Email = z.object({
  email: z.email(),
});

type EmailSchema = z.infer<typeof Email>;

export async function generateUniqueCodeDevice(payload: EmailSchema) {}
