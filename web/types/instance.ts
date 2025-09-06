export enum EInstanceEdition {
  COMMUNITY,
  CLOUD,
  ENTERPRISE,
}

export interface IInstanceInfo {
  instance: IInstance;
  config: IInstanceConfig;
}

export interface IInstance {
  id: string;
  slug: string;
  name: string;
  edition: EInstanceEdition;
  currentVersion: string;
  domain: string;
  namespace: string;
  is_setup_done: boolean;
  verified: boolean;
  test: boolean;
}

export interface IInstanceConfig {
  enableSignup: boolean;
  googleEnabled: boolean;
  githubEnabled: boolean;
  gitlabEnabled: boolean;
  magicLinkLoginEnabled: boolean;
  githubAppName: string;
}
