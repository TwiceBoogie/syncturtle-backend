import { createContext, ReactNode, useMemo, useReducer } from "react";

type ErrorPayload = {
  message: string;
  fieldErrors?: Record<string, string>;
};

type UserState = {
  user: { id: string; email: string; firstName: string; lastName: string } | null;
  isLoading: boolean;
  error: ErrorPayload | null;
};

const initialState: UserState = {
  user: null,
  isLoading: false,
  error: null,
};

type Action =
  | { type: "SET_USER"; payload: { id: string; email: string; firstName: string; lastName: string } }
  | { type: "LOGOUT" }
  | { type: "SET_LOADING"; payload: boolean }
  | { type: "SET_ERROR"; payload: ErrorPayload | null };

function userReducer(state: UserState, action: Action): UserState {
  switch (action.type) {
    case "SET_USER":
      return {
        ...state,
        user: {
          id: action.payload.id,
          email: action.payload.email,
          firstName: action.payload.firstName,
          lastName: action.payload.lastName,
        },
        isLoading: false,
        error: null,
      };
    case "LOGOUT":
      return {
        ...initialState,
      };
    case "SET_LOADING":
      // no-operation if value hasn't changed
      if (state.isLoading === action.payload) {
        return state;
      }
      return {
        ...state,
        isLoading: action.payload,
      };
    case "SET_ERROR":
      if (
        state.error?.message === action.payload?.message &&
        JSON.stringify(state.error?.fieldErrors) === JSON.stringify(action.payload?.fieldErrors)
      ) {
        return state;
      }
      return {
        ...state,
        error: action.payload,
      };
    default:
      return state;
  }
}

const UserContext = createContext<{ state: UserState; dispatch: React.Dispatch<Action> } | undefined>(undefined);

export const UserProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(userReducer, initialState);

  // recreate the context value if state changes
  // atleast it won't re-render on provider re-mounts if state hasn't actually changed
  const value = useMemo(() => ({ state, dispatch }), [state]);
  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
};

export default UserContext;
