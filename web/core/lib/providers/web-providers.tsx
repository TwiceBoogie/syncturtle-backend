"use client";

import { InstanceProvider } from "../context/instance-context";
import { PasswordProvider } from "../context/password-context";
import { UserProvider } from "../context/user-context";
import { ThemeProvider } from "../context/theme-context";

const providers = [
  (children: React.ReactNode) => <InstanceProvider>{children}</InstanceProvider>,
  (children: React.ReactNode) => <UserProvider>{children}</UserProvider>,
  (children: React.ReactNode) => <PasswordProvider>{children}</PasswordProvider>,
  (children: React.ReactNode) => <ThemeProvider>{children}</ThemeProvider>,
];
