// react
import { FC } from "react";
// @heroui
import { Accordion, AccordionItem } from "@heroui/accordion";
import { Button } from "@heroui/button";
import { Form } from "@heroui/form";
import { Input } from "@heroui/input";
// 3rd
import { CircleUserRound } from "lucide-react";

export const ProfileForm: FC = () => {
  const defaultContent =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";

  return (
    <>
      <Form>
        <div className="flex w-full flex-col gap-6">
          {/* picture */}
          <div className="relative h-44 w-full">
            <img
              src={"https://images.unsplash.com/photo-1506383796573-caf02b4a79ab"}
              className="h-44 w-full rounded-lg object-cover"
              alt="Cover image"
            />
            <div className="absolute -bottom-6 left-6 flex items-end justify-between">
              <div className="flex gap-3">
                <Button type="button" isIconOnly className="flex h-16 w-16 items-center justify-center rounded-lg">
                  <div className="h-full w-full rounded-md p-2">
                    <CircleUserRound className="h-full w-full" />
                  </div>
                </Button>
              </div>
            </div>
            <div className="absolute bottom-3 right-3 flex">
              <Button type="button" size="sm">
                Change cover
              </Button>
            </div>
          </div>
          <div className="item-center mt-6 flex justify-between">
            <div className="flex flex-col">
              <div className="item-center flex text-lg font-medium text-default-600/50">
                <span>Salvador Sebastian</span>
              </div>
              <span className="text-sm text-default-500/50">test@gmail.com</span>
            </div>
          </div>
          <div className="flex flex-col gap-2">
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-x-6 gap-y-4">
              <Input
                isRequired
                type="text"
                name="firstName"
                label="First Name"
                labelPlacement="outside"
                value="Salvador"
                size="sm"
              />
              <Input
                isRequired
                type="text"
                name="lastName"
                label="Last Name"
                labelPlacement="outside"
                value="Sebastian"
                size="sm"
              />
              <Input
                isRequired
                type="text"
                name="username"
                label="Display name"
                labelPlacement="outside"
                value="Salvador"
                size="sm"
              />
              <Input
                isRequired
                isDisabled
                type="email"
                name="email"
                label="Email"
                labelPlacement="outside"
                value="test@gmail.com"
                size="sm"
              />
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-x-6 gap-y-4">
              <Input />
            </div>
            <div className="flex items-center justify-between pt-6 pb-8">
              <Button type="button" size="sm" color="primary">
                Save changes
              </Button>
            </div>
          </div>
        </div>
      </Form>
      <Accordion>
        <AccordionItem key="deactivate" aria-label="Deactivate Account" title="Deactivate account">
          <div className="flex flex-col gap-8">
            <span className="text-sm tracking-tight">
              When deactivating an account, all of your data and resources will be permanently removed and this action
              can't be undone.
            </span>
            <div>
              <Button color="danger" size="sm">
                Deactivate account
              </Button>
            </div>
          </div>
        </AccordionItem>
      </Accordion>
    </>
  );
};
