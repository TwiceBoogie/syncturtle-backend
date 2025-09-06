import { IInstanceStore, InstanceStore } from "./instance.store";
import { IUserStore, UserStore } from "./user";

export class RootStore {
  user: IUserStore;
  instance: IInstanceStore;

  constructor() {
    this.user = new UserStore(this);
    this.instance = new InstanceStore();
  }
}
