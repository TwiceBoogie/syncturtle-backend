import { FC } from "react";
import { Info } from "lucide-react";
import { Alert } from "@heroui/alert";
import { TAuthErrorInfo } from "@/helpers/authentication.helper";

type TAuthBanner = {
  bannerData: TAuthErrorInfo | undefined;
  handleBannerData?: (bannerData: TAuthErrorInfo | undefined) => void;
};

export const AuthBanner: FC<TAuthBanner> = (props) => {
  const { bannerData, handleBannerData } = props;

  if (!bannerData) return <></>;
  return (
    <Alert color="danger" title={bannerData.message} onClose={() => handleBannerData && handleBannerData(undefined)} />
  );
};
