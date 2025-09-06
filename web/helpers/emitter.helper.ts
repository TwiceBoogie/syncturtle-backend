export type Unsub = () => void;
export class Emitter {
  private listeners = new Set<() => void>();
  subscribe = (cb: () => void): Unsub => {
    this.listeners.add(cb);
    return () => this.listeners.delete(cb);
  };
  emit = () => this.listeners.forEach((cb) => cb());
}
