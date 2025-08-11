export type Result<T, E = unknown> = { ok: true; data: T } | { ok: false; error: E };
