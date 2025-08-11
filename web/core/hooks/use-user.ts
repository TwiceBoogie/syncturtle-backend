import UserContext from "@/lib/context/user-context";
import { useCallback, useContext, useMemo } from "react";

export function useUser() {
  const context = useContext(UserContext);
  if (!context) throw new Error("useUser must be used within a UserProvider");

  // const { state, dispatch } = context;

  // const fetchCurrentUser = useCallback(async () => {
  //   dispatch({ type: "SET_LOADING", payload: true });
  //   dispatch({ type: "SET_ERROR", payload: null });
  //   try {
  //     const data = await userService.getCurrentUser();
  //     console.log("should be my user data", data);
  //     dispatch({ type: "SET_USER", payload: data.user });
  //     return data;
  //   } catch (error) {
  //     dispatch({
  //       type: "SET_ERROR",
  //       payload: {
  //         message: error instanceof ApiError ? error.message : "Unexpected error",
  //         fieldErrors: error instanceof ApiError ? error.errors ?? {} : undefined,
  //       },
  //     });
  //   } finally {
  //     dispatch({ type: "SET_LOADING", payload: false });
  //   }
  // }, [dispatch]);

  // const signOut = useCallback(async (): Promise<void> => {
  //   dispatch({ type: "LOGOUT" });
  // }, [dispatch]);

  // return {
  //   data: state.user,
  //   isLoading: state.isLoading,
  //   error: state.error,
  //   fetchCurrentUser,
  //   signOut,
  // };
  // ② This memo ensures the returned object’s reference only changes
  //    when one of its dependencies (data, isLoading, error, fetchCurrentUser, signOut) changes.
  // return useMemo(
  //   () => ({
  //     data: state.user,
  //     isLoading: state.isLoading,
  //     error: state.error,
  //     fetchCurrentUser,
  //     signOut,
  //   }),
  //   [state.user, state.isLoading, state.error, fetchCurrentUser, signOut]
  // );
  return context;
}

export function useUserStore() {
  const ctx = useContext(UserContext);
  if (!ctx) throw new Error("useUserStore must be used within UserProvider");
  return ctx;
}
