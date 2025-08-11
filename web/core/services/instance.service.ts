import { TInstanceInfo } from "@/lib/context/instance-context";
import { APIService } from "./api.service";
import { API_BASE_URL } from "@/helpers/common.helper";

export class InstanceService extends APIService {
  constructor() {
    super(`${API_BASE_URL}/api/instances`);
  }

  getInstanceInfo() {
    return this.get<TInstanceInfo>("/", {}, { supressRedirect: true });
  }
}
