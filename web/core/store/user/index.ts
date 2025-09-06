import { UserService } from "@/services/user.service";
import { IAuthUser } from "@/types/authentication";
import { RootStore } from "../root.store";
import { IUserProfileStore, ProfileStore } from "./profile.store";
import { Emitter, Unsub } from "@/helpers/emitter.helper";
import { TErrorPayload } from "@/types/common";

type TSnapshot = {
  isAuthenticated: boolean;
  data: IAuthUser | undefined;
  isLoading: boolean;
  error: TErrorPayload | undefined;
};

export interface IUserStore {
  // uSES need these
  subscribe(cb: () => void): Unsub;
  getSnapshot(): TSnapshot;
  getServerSnapshot(): TSnapshot;
  // store
  userProfile: IUserProfileStore;
  // action
  fetchCurrentUser: () => Promise<IAuthUser | undefined>;
  signOut: () => Promise<void>;
}

const initial: TSnapshot = {
  isAuthenticated: false,
  data: undefined,
  isLoading: false,
  error: undefined,
};

export class UserStore implements IUserStore {
  // PRIVATE internals
  private _snap: TSnapshot = initial;
  private inflight: Promise<IAuthUser | undefined> | null = null;
  private emitter;
  private userService: UserService;
  userProfile: IUserProfileStore;

  constructor(private rootStore: RootStore) {
    // services
    this.userService = new UserService();
    this.emitter = new Emitter();
    this.userProfile = new ProfileStore(rootStore);
  }

  // uSES API (public)
  // arrow func keep 'this'
  public subscribe = (cb: () => void): Unsub => this.emitter.subscribe(cb);
  public getSnapshot = (): TSnapshot => this._snap;
  public getServerSnapshot = (): TSnapshot => initial;

  public fetchCurrentUser = async (): Promise<IAuthUser | undefined> => {
    if (this.inflight) return this.inflight;
    this.set({ isLoading: true, error: undefined });

    this.inflight = (async () => {
      try {
        const { user } = await this.userService.getCurrentUser();
        this.set({ data: user, isLoading: false, error: undefined });

        if (user && user.id) {
          await Promise.all([this.userProfile.fetchUserProfile()]);
        }
        return user;
      } catch (error) {
        this.set({ isLoading: false, error: { message: "hi", fieldErrors: {} } });
        throw error;
      } finally {
        this.inflight = null;
      }
    })();

    return this.inflight;
  };

  public signOut = async (): Promise<void> => {};

  private set(patch: Partial<TSnapshot>) {
    this._snap = { ...this._snap, ...patch }; // immutbale replace
    this.emitter.emit();
  }
}
