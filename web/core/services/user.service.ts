import type { IAuthUser } from "@/types/authentication";
import { APIService } from "./api.service";

export class UserService extends APIService {
  constructor() {
    super("http://localhost:8000/ui/v1/user");
  }

  getCurrentUser() {
    return this.get<IAuthUser>("/token", {}, { supressRedirect: true });
  }
}
