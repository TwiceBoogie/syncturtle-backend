"use client";

import { InstanceService } from "@/services/instance.service";
import { createContext, ReactNode, useCallback, useMemo, useReducer } from "react";

enum EInstanceEdition {
  COMMUNITY,
  CLOUD,
  ENTERPRISE,
}

type TError = {
  status: string;
  message: string;
  data?: {
    is_activated: boolean;
    is_setup_done: boolean;
  };
};

export type TInstanceInfo = {
  instance: TInstance;
  config: TConfig;
};

type TInstance = {
  id: string;
  slug: string;
  name: string;
  edition: EInstanceEdition;
  currentVersion: string;
  domain: string;
  namespace: string;
  is_setup_done: boolean;
  verified: boolean;
  test: boolean;
};

type TConfig = {
  enableSignup: boolean;
  googleEnabled: boolean;
  githubEnabled: boolean;
  gitlabEnabled: boolean;
  magicLinkLoginEnabled: boolean;
  githubAppName: string;
};

type TInstanceContext = {
  instance: TInstance | undefined;
  config: TConfig | undefined;
  isLoading: boolean;
  error: TError | undefined;
  fetchInstanceInfo: () => Promise<void>;
};

type TInstanceState = {
  instance: TInstance | undefined;
  config: TConfig | undefined;
  isLoading: boolean;
  error: TError | undefined;
};

const initialState: TInstanceState = {
  instance: undefined,
  config: undefined,
  isLoading: false,
  error: undefined,
};

type TAction =
  | { type: "FETCH_START" }
  | { type: "FETCH_SUCCESS"; payload: TInstanceInfo }
  | { type: "FETCH_ERROR"; payload: TError };

function instanceReducer(state: TInstanceState, action: TAction): TInstanceState {
  switch (action.type) {
    case "FETCH_START":
      return {
        ...state,
        isLoading: true,
      };
    case "FETCH_SUCCESS":
      return {
        ...state,
        instance: action.payload.instance,
        config: action.payload.config,
        isLoading: false,
      };
    case "FETCH_ERROR":
      return {
        ...state,
        error: action.payload,
        isLoading: false,
      };
    default:
      return state;
  }
}

const InstanceContext = createContext<TInstanceContext | null>(null);

const instanceService = new InstanceService();
export const InstanceProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(instanceReducer, initialState);

  const fetchInstanceInfo = useCallback(async () => {
    dispatch({ type: "FETCH_START" });
    try {
      const data = await instanceService.getInstanceInfo();
      dispatch({ type: "FETCH_SUCCESS", payload: data });
    } catch (err) {
      const error = {
        status: "error",
        message: "Failed to fetch instance info",
      };
      dispatch({ type: "FETCH_ERROR", payload: error });
      throw err;
    }
  }, []);

  const value = useMemo(() => ({ ...state, fetchInstanceInfo }), [state, fetchInstanceInfo]);
  return <InstanceContext.Provider value={value}>{children}</InstanceContext.Provider>;
};

export default InstanceContext;
