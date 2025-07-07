import PasswordContext from "@/lib/context/password-context";
import { PasswordService } from "@/services/password.service";
import { useCallback, useContext, useMemo } from "react";

const passwordService = new PasswordService();
export function usePassword() {
  const context = useContext(PasswordContext);
  if (!context) throw new Error("usePassword must be used within a PasswordProvider");

  const { state, dispatch } = context;

  const fetchEncryptionKeys = useCallback(async () => {
    dispatch({ type: "SET_LOADING", payload: true });
    dispatch({ type: "SET_ERROR", payload: null });

    try {
      const data = await passwordService.getEncryptionKeys();
      console.log(data);
      dispatch({ type: "SET_ENCRYPTION_LIST", payload: data });
      return data;
    } catch (error) {
      console.log(error);
      dispatch({ type: "SET_ERROR", payload: { message: "Something went wrong" } });
    } finally {
      dispatch({ type: "SET_LOADING", payload: false });
    }
  }, [dispatch]);

  return useMemo(
    () => ({
      data: state.encryptionKey,
      isLoading: state.isLoading,
      error: state.error,
      fetchEncryptionKeys,
    }),
    [state.encryptionKey, state.isLoading, state.error, fetchEncryptionKeys]
  );
}

export function usePasswordStore() {
  const ctx = useContext(PasswordContext);
  if (!ctx) throw new Error("usePasswordStore must be used within PasswordProvider");
  return ctx;
}
