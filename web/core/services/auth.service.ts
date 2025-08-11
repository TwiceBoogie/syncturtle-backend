import { API_AUTH_ENDPOINT } from "@/constants";
import { APIService } from "./api.service";
import { ICsrfTokenData, IEmailCheckData, IEmailCheckResponse } from "@/types/authentication";

export class AuthService extends APIService {
  constructor() {
    super(API_AUTH_ENDPOINT);
  }

  async requestCsrfToken(): Promise<ICsrfTokenData> {
    return this.get<ICsrfTokenData>("/auth/get-csrf-token")
      .then((response) => response)
      .catch((error) => {
        throw error;
      });
  }

  async emailCheck(data: IEmailCheckData): Promise<IEmailCheckResponse> {
    return this.post<IEmailCheckResponse>("/check-email", data)
      .then((response) => response)
      .catch((error) => {
        throw error;
      });
  }

  async generateUniqueCode(data: { email: string }): Promise<any> {
    return this.post("/auth/magic-generate/", data)
      .then((response) => response)
      .catch((error) => {
        throw error?.response?.data;
      });
  }
}
