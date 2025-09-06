import { clearAccessToken, getAccessToken } from "./tokenClient";

export async function secureFetch<T = unknown>(input: RequestInfo, init: RequestInit = {}) {
  let token = await getAccessToken();
  let res = await fetch(input, withAuth(init, token));

  if (res.status === 401) {
    clearAccessToken(); // maybe it expired
    token = await getAccessToken(); // refresh one
    res = await fetch(input, withAuth(init, token));
  }
  return res.json() as Promise<T>;
}

function withAuth(init: RequestInit, token: string): RequestInit {
  const headers = new Headers(init.headers as HeadersInit | undefined);
  headers.set("Authorization", `Bearer ${token}`);
  return { ...init, headers };
}
