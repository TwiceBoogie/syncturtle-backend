import { APIService } from "./api.service";
import { API_BASE_URL } from "@/helpers/common.helper";
import { IInstanceInfo } from "@/types/instance";

export class InstanceService extends APIService {
  constructor() {
    super(`${API_BASE_URL}/ui/v1/instance`);
  }

  getInstanceInfo() {
    return this.get<IInstanceInfo>("", {}, { supressRedirect: true });
  }
}
