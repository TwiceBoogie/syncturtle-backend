export interface IAuthUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface IAuthUserToken {
  user: IAuthUser;
}

export interface IEmailCheckData {
  email: string;
}

export interface ICsrfTokenData {
  csrfToken: string;
}

export interface IEmailCheckResponse {
  status: "MAGIC_CODE" | "CREDENTIAL";
  existing: boolean;
  passwordAutoSet: boolean;
}

export type TEmailCheckResponse = {
  status: "MAGIC_CODE" | "CREDENTIAL";
  existing: boolean;
  passwordAutoSet: boolean;
};

export interface IRegistrationResponse {
  message: string;
  deviceToken: string;
  token: string;
}

export type TRegistrationResponse = {
  message: string;
  deviceToken: string;
  token: string;
};

export interface IAuthErrorResponse {
  error_code: number;
  error_message: string;
  payload?: Record<string, any>;
}

export type TMagicCodeResponse = TRegistrationResponse | IAuthErrorResponse;
export type TEmailCheckResult = TEmailCheckResponse | IAuthErrorResponse;
