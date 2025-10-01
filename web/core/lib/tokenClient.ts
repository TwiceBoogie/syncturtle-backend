"use client";

import { API_BASE_URL } from "@/helpers/common.helper";

let at: string | null = null;
let expMs = 0; // epoch ms of AT expiration
let inflight: Promise<string> | null = null;
const LEEWAY_MS = 60_000;

function parseJwtExpMs(jwt: string): number {
  // convert base64url -> base64
  const payload = JSON.parse(atob(jwt.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
  return (payload.exp || 0) * 1000;
}

async function doRefresh(): Promise<string> {
  const csrf =
    document.cookie
      .split("; ")
      .find((c) => c.startsWith("__Host-csrf="))
      ?.split("=")[1] || "";
  const res = await fetch(`${API_BASE_URL}/ui/v1/auth/refresh`, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json", "X-CSRF-TOKEN": csrf },
    body: JSON.stringify({ grantType: "refresh_token" }),
  });
  if (!res.ok) throw new Error("refresh failed");
  const { accessToken } = await res.json();
  at = accessToken;
  expMs = parseJwtExpMs(accessToken);
  return at!;
}

export async function getAccessToken(): Promise<string> {
  const now = Date.now();
  if (at && now < expMs - LEEWAY_MS) return at;
  if (!inflight)
    inflight = doRefresh().finally(() => {
      inflight = null;
    });
  return inflight;
}

export function clearAccessToken() {
  at = null;
  expMs = 0;
}
