import { IEncryptionKey } from "@/types/password";
import { APIService } from "./api.service";

export class PasswordService extends APIService {
  constructor() {
    super("http://localhost:8000/ui/v1/password");
  }

  getEncryptionKeys() {
    return this.get<IEncryptionKey[]>("", {}, { supressRedirect: true });
  }
}
