import type { IAuthUser, IAuthUserToken } from "@/types/authentication";
import { APIService } from "./api.service";

export class UserService extends APIService {
  constructor() {
    super("http://localhost:8000/ui/v1/user");
  }

  getCurrentUser() {
    return this.get<IAuthUserToken>("/token", {}, { supressRedirect: true });
  }
}
