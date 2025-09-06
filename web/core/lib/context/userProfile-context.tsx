import { createContext, ReactNode, useCallback, useMemo, useReducer } from "react";
import { UserService } from "@/services/user.service";
import { IUserProfile } from "@/types/userProfile";

type TErrorPayload = {
  message: string;
  fieldErrors?: Record<string, string>;
};

type TUserProfileContext = {
  data: IUserProfile | null;
  isLoading: boolean;
  error: TErrorPayload | null;
  fetchUserProfile: () => Promise<IUserProfile | null>;
};

type TUserProfileState = {
  profile: IUserProfile | null;
  isLoading: boolean;
  error: TErrorPayload | null;
};

const initialState: TUserProfileState = {
  profile: null,
  isLoading: false,
  error: null,
};

type TAction =
  | { type: "FETCH_START" }
  | { type: "FETCH_SUCCESS"; payload: IUserProfile }
  | { type: "LOGOUT" }
  | { type: "FETCH_FAIL"; payload: TErrorPayload | null };

function userProfileReducer(state: TUserProfileState, action: TAction) {
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
        profile: action.payload,
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

const UserProfileContext = createContext<TUserProfileContext | null>(null);

const userService = new UserService();
export const UserProfileProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(userProfileReducer, initialState);

  const fetchUserProfile = useCallback(async () => {
    dispatch({ type: "FETCH_START" });
    try {
      const data = await userService.getCurrentUserProfile();
      dispatch({ type: "FETCH_SUCCESS", payload: data });
      return data;
    } catch (error) {
      dispatch({
        type: "FETCH_FAIL",
        payload: {
          message: "user-profile-fetch-error",
          fieldErrors: undefined,
        },
      });
      throw error;
    }
  }, []);

  const value = useMemo(
    () => ({ data: state.profile, isLoading: state.isLoading, error: state.error, fetchUserProfile }),
    [state, fetchUserProfile]
  );
  return <UserProfileContext.Provider value={value}>{children}</UserProfileContext.Provider>;
};
