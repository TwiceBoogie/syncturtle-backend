import { API_AUTH_ENDPOINT } from "@/constants";
import { APIService } from "./api.service";

export class AuthService extends APIService {
  constructor() {
    super(API_AUTH_ENDPOINT);
  }
}
