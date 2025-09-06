import { Emitter, Unsub } from "@/helpers/emitter.helper";
import { InstanceService } from "@/services/instance.service";
import { TErrorPayload } from "@/types/common";
import { IInstance, IInstanceConfig } from "@/types/instance";

type TError = {
  status: string;
  message: string;
  data?: {
    is_activated: boolean;
    is_setup_done: boolean;
  };
};

type TSnapshot = {
  instance: IInstance | undefined;
  config: IInstanceConfig | undefined;
  isLoading: boolean;
  error: TError | undefined;
};

export interface IInstanceStore {
  // uSES need these
  subscribe(cb: () => void): Unsub;
  getSnapshot(): TSnapshot;
  getServerSnapshot(): TSnapshot;
  // store
  // action
  fetchInstanceInfo: () => Promise<void>;
}

const initial: TSnapshot = {
  instance: undefined,
  config: undefined,
  isLoading: false,
  error: undefined,
};

export class InstanceStore implements IInstanceStore {
  // PRIVATE internals;
  private _snap: TSnapshot = initial;
  private inflight: Promise<void> | null = null;
  private emitter: Emitter;
  private instanceService: InstanceService;

  constructor() {
    this.emitter = new Emitter();
    this.instanceService = new InstanceService();
  }

  // arrow func keep 'this'
  public subscribe = (cb: () => void): Unsub => this.emitter.subscribe(cb);
  public getSnapshot = (): TSnapshot => this._snap;
  public getServerSnapshot = (): TSnapshot => initial;

  public fetchInstanceInfo = async (): Promise<void> => {
    if (this.inflight) return this.inflight;
    this.set({ isLoading: true, error: undefined });

    this.inflight = (async () => {
      try {
        const instanceInfo = await this.instanceService.getInstanceInfo();
        this.set({ instance: instanceInfo.instance, config: instanceInfo.config, isLoading: false });
      } catch (error) {
        this.set({
          isLoading: false,
          error: {
            status: "error",
            message: "Failed to fetch instance info",
          },
        });
        throw error;
      } finally {
        this.inflight = null;
      }
    })();

    return this.inflight;
  };

  private set(patch: Partial<TSnapshot>) {
    this._snap = { ...this._snap, ...patch }; // immutable replace
    this.emitter.emit();
  }
}
