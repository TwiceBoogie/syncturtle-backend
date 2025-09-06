export interface IOnboardingSteps {
  profileComplete: boolean;
  workspaceCreated: boolean;
  workspaceInvite: boolean;
  workspaceJoin: boolean;
}

export interface IUserProfile {
  id: string;
  user: string;
  role: string;
  isOnboarded: boolean;
  onboardingStep: IOnboardingSteps;
  billingAddressCountry: string;
  billingAddress: string;
  hasBillingAddress: boolean;
  companyName: string;
  createdAt: Date;
  updatedAt: Date;
}
