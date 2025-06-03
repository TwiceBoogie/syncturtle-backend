import UserContext from "@/lib/context/user-context";
import { ApiError } from "@/lib/errors/api-error";
import { UserService } from "@/services/user.service";
import { useCallback, useContext } from "react";

const userService = new UserService();
export function useUser() {
  const context = useContext(UserContext);
  if (!context) throw new Error("useUser must be used within a UserProvider");

  const { state, dispatch } = context;

  const fetchCurrentUser = useCallback(async () => {
    dispatch({ type: "SET_LOADING", payload: true });
    dispatch({ type: "SET_ERROR", payload: null });
    userService
      .getCurrentUser()
      .then((data) => dispatch({ type: "SET_USER", payload: data }))
      .catch((error) => {
        if (error instanceof ApiError) {
          dispatch({
            type: "SET_ERROR",
            payload: {
              message: error.message,
              fieldErrors: error.errors ?? {},
            },
          });
        } else {
          dispatch({
            type: "SET_ERROR",
            payload: {
              message: "Unexpected error",
            },
          });
        }
      })
      .finally(() => dispatch({ type: "SET_LOADING", payload: false }));
  }, [dispatch]);

  const logout = () => {
    dispatch({ type: "LOGOUT" });
  };

  return {
    user: state.user,
    isLoading: state.isLoading,
    error: state.error,
    fetchCurrentUser,
    logout,
  };
}
