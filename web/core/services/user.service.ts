import type { IAuthUser, IAuthUserToken } from "@/types/authentication";
import { APIService } from "./api.service";
import { API_BASE_URL } from "@/helpers/common.helper";
import { IUserProfile } from "@/types/userProfile";
import { secureFetch } from "@/lib/secureFetch";

export class UserService extends APIService {
  constructor() {
    super(API_BASE_URL);
  }

  getCurrentUser(): Promise<IAuthUserToken> {
    return this.get<IAuthUserToken>("/ui/v1/user/token", {}, { supressRedirect: true });
  }

  async getCurrentUserProfile(): Promise<IUserProfile> {
    return secureFetch(`${this.baseURL}/ui/v1/user/profile`, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });
  }
}
