export class RootStore {
  user: IUserStore;
  instance: IInstanceStore;

  constructor() {
    this.user = new UserStore();
    this.instance = new InstanceStore();
  }
}
