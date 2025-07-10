import { UserService } from "@/services/user.service";
import { IAuthUser } from "@/types/authentication";
import { createContext, ReactNode, useCallback, useMemo, useReducer } from "react";
import { ApiError } from "../errors/api-error";

type ErrorPayload = {
  message: string;
  fieldErrors?: Record<string, string>;
};

type UserContextType = {
  data: IAuthUser | null;
  isLoading: boolean;
  error: ErrorPayload | null;
  fetchCurrentUser: () => Promise<IAuthUser | undefined>;
  signOut: () => Promise<void>;
};

type UserState = {
  user: IAuthUser | null;
  isLoading: boolean;
  error: ErrorPayload | null;
};

const initialState: UserState = {
  user: null,
  isLoading: false,
  error: null,
};

type Action =
  | { type: "FETCH_START" }
  | { type: "FETCH_SUCCESS"; payload: IAuthUser }
  | { type: "LOGOUT" }
  | { type: "FETCH_FAIL"; payload: ErrorPayload | null };

function userReducer(state: UserState, action: Action): UserState {
  switch (action.type) {
    case "FETCH_START":
      return {
        ...state,
        isLoading: true,
        error: null,
      };
    case "FETCH_SUCCESS":
      return {
        ...state,
        user: action.payload,
        isLoading: false,
        error: null,
      };
    case "LOGOUT":
      return {
        ...initialState,
      };
    case "FETCH_FAIL":
      return {
        ...state,
        isLoading: false,
        error: action.payload,
      };
    default:
      return state;
  }
}

const UserContext = createContext<UserContextType | null>(null);

export const UserProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(userReducer, initialState);

  const userService = new UserService();

  const fetchCurrentUser = useCallback(async () => {
    dispatch({ type: "FETCH_START" });
    try {
      const data = await userService.getCurrentUser();
      dispatch({ type: "FETCH_SUCCESS", payload: data.user });
      return data.user;
    } catch (error) {
      dispatch({
        type: "FETCH_FAIL",
        payload: {
          message: error instanceof ApiError ? error.message : "Unexpected error",
          fieldErrors: error instanceof ApiError ? error.errors ?? {} : undefined,
        },
      });
    }
  }, []);

  const signOut = useCallback(async (): Promise<void> => {
    dispatch({ type: "LOGOUT" });
  }, []);

  // recreate the context value if state changes
  // atleast it won't re-render on provider re-mounts if state hasn't actually changed
  const value = useMemo(
    () => ({ data: state.user, isLoading: state.isLoading, error: state.error, fetchCurrentUser, signOut }),
    [state, fetchCurrentUser, signOut]
  );
  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
};

export default UserContext;
