export interface IAuthUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface IEmailCheckData {
  email: string;
}

export interface IEmailCheckResponse {
  status: "MAGIC_CODE" | "CREDENTIAL";
  existing: boolean;
  passwordAutoSet: boolean;
}

export interface IRegistrationResponse {
  message: string;
  deviceToken: string;
  token: string;
}
