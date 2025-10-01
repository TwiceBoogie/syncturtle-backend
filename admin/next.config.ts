import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  trailingSlash: true,
  /* config options here */
  basePath: process.env.NEXT_PUBLIC_ADMIN_BASE_PATH || "",
};

export default nextConfig;
