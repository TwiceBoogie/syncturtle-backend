import { TAuthErrorInfo } from "@/helpers/authentication.helper";
import { FC } from "react";

type TAuthBanner = {
  bannerData: TAuthErrorInfo | undefined;
  handleBannerData?: (bannerData: TAuthErrorInfo | undefined) => void;
};

export const AuthBanner: FC<TAuthBanner> = (props) => {
  const { bannerData, handleBannerData } = props;

  if (!bannerData) return <></>;
  return <div></div>;
};
