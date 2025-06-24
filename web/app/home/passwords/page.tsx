import { Card, CardHeader, CardBody, CardFooter } from "@heroui/card";
import { ERowVariant, Row } from "@/components/core/ui/row";
import { PasswordHeader } from "@/components/passwords/header";
import { PageHead } from "@/components/core";

export default function page() {
  const isPasswordInfoLoaded = true;

  return (
    <>
      <PageHead />
      <div className="w-full h-full overflow-hidden overflow-y-auto">
        {!isPasswordInfoLoaded ? (
          <div className="w-full h-screen flex justify-center items-center bg-red-500">No password chosen</div>
        ) : (
          <div className="relative w-full h-full overflow-hidden flex flex-col">
            <Row className="h-[3.75rem] border-b border-custom-border-200 flex">
              <PasswordHeader />
            </Row>
            <Card className="h-full">
              <CardHeader>hello</CardHeader>
              <CardBody>yes</CardBody>
              <CardFooter>there</CardFooter>
            </Card>
          </div>
        )}
      </div>
    </>
  );
}
