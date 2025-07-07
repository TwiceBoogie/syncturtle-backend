"use server";

import { cookies } from "next/headers";

export async function logout() {
  (await cookies()).set("token", "", {
    path: "/",
    httpOnly: true,
    sameSite: "strict",
    maxAge: 0,
  });
  (await cookies()).set("XSRF-TOKEN", "", {
    path: "/",
    httpOnly: true,
    sameSite: "strict",
    maxAge: 0,
  });
}
