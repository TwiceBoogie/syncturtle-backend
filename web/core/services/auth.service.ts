import { API_AUTH_ENDPOINT } from "@/constants";
import { APIService } from "./api.service";
import { ICsrfTokenData, IEmailCheckData, IEmailCheckResponse } from "@/types/authentication";
import { API_BASE_URL } from "@/helpers/common.helper";

export class AuthService extends APIService {
  constructor() {
    super(API_BASE_URL);
  }

  async requestCsrfToken(): Promise<ICsrfTokenData> {
    return this.get<ICsrfTokenData>("/api/csrf-token")
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
