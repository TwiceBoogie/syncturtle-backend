import { JSX } from "react";

const BuildProviderTree = (providerFns: Array<(children: React.ReactNode) => JSX.Element>) => {
  return function ProviderTree({ children }: { children: React.ReactNode }) {
    return providerFns.reduceRight((acc, fn) => fn(acc), children);
  };
};

export default BuildProviderTree;
