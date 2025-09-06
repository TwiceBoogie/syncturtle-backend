export type Result<T, E = unknown> = { ok: true; data: T } | { ok: false; error: E };

export type TErrorPayload = {
  message: string;
  fieldErrors?: Record<string, string>;
};
