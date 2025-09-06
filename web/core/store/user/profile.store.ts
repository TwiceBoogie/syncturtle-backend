import { UserService } from "@/services/user.service";
import { IUserProfile } from "@/types/userProfile";
import { RootStore } from "../root.store";

type TErrorPayload = {
  message: string;
  fieldErrors?: Record<string, string>;
};

type TSnapshot = {
  data: IUserProfile | undefined;
  isLoading: boolean;
  error: TErrorPayload | undefined;
};

type Unsub = () => void;
class Emitter {
  private listeners = new Set<() => void>();
  subscribe = (cb: () => void): Unsub => {
    this.listeners.add(cb);
    return () => this.listeners.delete(cb);
  };
  emit = () => this.listeners.forEach((cb) => cb());
}

export interface IUserProfileStore {
  // uSES need these
  subscribe(cb: () => void): Unsub;
  getSnapshot(): TSnapshot;
  getServerSnapshot(): TSnapshot;
  // action
  fetchUserProfile: () => Promise<IUserProfile | undefined>;
  signOut: () => Promise<void>;
}

const initial: TSnapshot = {
  data: undefined,
  isLoading: false,
  error: undefined,
};

export class ProfileStore implements IUserProfileStore {
  // PRIVATE internals
  private _snap: TSnapshot = initial;
  private inflight: Promise<IUserProfile | undefined> | null = null;
  private emitter;
  private userService: UserService;

  constructor(public store: RootStore) {
    this.emitter = new Emitter();
    this.userService = new UserService();
  }

  // arrow func keep 'this'
  public subscribe = (cb: () => void): Unsub => this.emitter.subscribe(cb);
  public getSnapshot = (): TSnapshot => this._snap;
  public getServerSnapshot = (): TSnapshot => initial;

  public fetchUserProfile = async (): Promise<IUserProfile | undefined> => {
    if (this.inflight) return this.inflight;
    this.set({ isLoading: true, error: undefined });

    this.inflight = (async () => {
      try {
        const userProfile = await this.userService.getCurrentUserProfile();
        this.set({ data: userProfile, isLoading: false, error: undefined });

        return userProfile;
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
