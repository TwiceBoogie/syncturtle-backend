import { createContext, ReactNode, useMemo, useReducer } from "react";

type ErrorPayload = {
  message: string;
  fieldErrors?: Record<string, string>;
};

type TEncryptionKey = {
  id: string;
  username: string;
  description: string;
};

type PasswordState = {
  encryptionKey: TEncryptionKey[];
  isLoading: boolean;
  error: ErrorPayload | null;
};

const initialState: PasswordState = {
  encryptionKey: [],
  isLoading: false,
  error: null,
};

type Action =
  | { type: "SET_ENCRYPTION_LIST"; payload: TEncryptionKey[] }
  | { type: "SET_LOADING"; payload: boolean }
  | { type: "SET_ERROR"; payload: ErrorPayload | null }
  | { type: "RESET" };

function passwordReducer(state: PasswordState, action: Action): PasswordState {
  switch (action.type) {
    case "SET_ENCRYPTION_LIST":
      return {
        ...state,
        encryptionKey: action.payload,
        isLoading: false,
        error: null,
      };
    case "SET_LOADING":
      return {
        ...state,
        isLoading: action.payload,
      };
    case "SET_ERROR":
      return {
        ...state,
        error: action.payload,
        isLoading: false,
      };
    case "RESET":
      return {
        ...initialState,
      };
    default:
      return state;
  }
}

const PasswordContext = createContext<{ state: PasswordState; dispatch: React.Dispatch<Action> } | undefined>(
  undefined
);

export const PasswordProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(passwordReducer, initialState);

  const value = useMemo(() => ({ state, dispatch }), [state]);
  return <PasswordContext.Provider value={value}>{children}</PasswordContext.Provider>;
};

export default PasswordContext;
