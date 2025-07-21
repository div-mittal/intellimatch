import type { NextConfig } from "next";
import dotenv from "dotenv";
dotenv.config();

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
      source: "/api/:path*",
      destination: `${process.env.BACKEND_URL}/api/:path*`, 
      },
    ];
  }, 
};

export default nextConfig;
