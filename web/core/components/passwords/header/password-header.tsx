"use client";

import { FC } from "react";

import { Header } from "@/components/core/ui/header";
import { Button } from "@heroui/button";

export const PasswordHeader: FC = () => {
  return (
    <Header className="my-auto">
      <Header.LeftItem>name of password</Header.LeftItem>
      <Header.RightItem>
        <Button>Save</Button>
      </Header.RightItem>
    </Header>
  );
};
