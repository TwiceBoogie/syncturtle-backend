// lib/context/theme-context.tsx
import { createContext, ReactNode, useReducer, useMemo, useEffect } from "react";

//
// 1) Define your state shape: each flag is boolean|undefined
//
export type ThemeState = {
  sidebarCollapsed: boolean | undefined;
  extendedSidebarCollapsed: boolean | undefined;
  extendedProjectSidebarCollapsed: boolean | undefined;
  profileSidebarCollapsed: boolean | undefined;
  workspaceAnalyticsSidebarCollapsed: boolean | undefined;
  issueDetailSidebarCollapsed: boolean | undefined;
  epicDetailSidebarCollapsed: boolean | undefined;
  initiativesSidebarCollapsed: boolean | undefined;
  projectOverviewSidebarCollapsed: boolean | undefined;
};

//
// 2) Start with everything undefined
//
const initialThemeState: ThemeState = {
  sidebarCollapsed: undefined,
  extendedSidebarCollapsed: undefined,
  extendedProjectSidebarCollapsed: undefined,
  profileSidebarCollapsed: undefined,
  workspaceAnalyticsSidebarCollapsed: undefined,
  issueDetailSidebarCollapsed: undefined,
  epicDetailSidebarCollapsed: undefined,
  initiativesSidebarCollapsed: undefined,
  projectOverviewSidebarCollapsed: undefined,
};

// 3) A union of all toggle action types
type ToggleActionType =
  | "TOGGLE_SIDEBAR"
  | "TOGGLE_EXTENDED_SIDEBAR"
  | "TOGGLE_EXTENDED_PROJECT_SIDEBAR"
  | "TOGGLE_PROFILE_SIDEBAR"
  | "TOGGLE_WORKSPACE_ANALYTICS_SIDEBAR"
  | "TOGGLE_ISSUE_DETAIL_SIDEBAR"
  | "TOGGLE_EPIC_DETAIL_SIDEBAR"
  | "TOGGLE_INITIATIVES_SIDEBAR"
  | "TOGGLE_PROJECT_OVERVIEW_SIDEBAR";

type ThemeAction = {
  type: ToggleActionType;
  payload?: boolean;
};

//
// 4) A helper that given an action type, returns both the `state` key
//    (e.g. "sidebarCollapsed") and the exact localStorage key
//    (e.g. "app_sidebar_collapsed") we want to read/write.
//
function lookupKey(type: ToggleActionType): { stateKey: keyof ThemeState; lsKey: string } {
  switch (type) {
    case "TOGGLE_SIDEBAR":
      return { stateKey: "sidebarCollapsed", lsKey: "app_sidebar_collapsed" };
    case "TOGGLE_EXTENDED_SIDEBAR":
      return {
        stateKey: "extendedSidebarCollapsed",
        lsKey: "extended_sidebar_collapsed",
      };
    case "TOGGLE_EXTENDED_PROJECT_SIDEBAR":
      return {
        stateKey: "extendedProjectSidebarCollapsed",
        lsKey: "extended_project_sidebar_collapsed",
      };
    case "TOGGLE_PROFILE_SIDEBAR":
      return {
        stateKey: "profileSidebarCollapsed",
        lsKey: "profile_sidebar_collapsed",
      };
    case "TOGGLE_WORKSPACE_ANALYTICS_SIDEBAR":
      return {
        stateKey: "workspaceAnalyticsSidebarCollapsed",
        lsKey: "workspace_analytics_sidebar_collapsed",
      };
    case "TOGGLE_ISSUE_DETAIL_SIDEBAR":
      return {
        stateKey: "issueDetailSidebarCollapsed",
        lsKey: "issue_detail_sidebar_collapsed",
      };
    case "TOGGLE_EPIC_DETAIL_SIDEBAR":
      return {
        stateKey: "epicDetailSidebarCollapsed",
        lsKey: "epic_detail_sidebar_collapsed",
      };
    case "TOGGLE_INITIATIVES_SIDEBAR":
      return {
        stateKey: "initiativesSidebarCollapsed",
        lsKey: "initiatives_sidebar_collapsed",
      };
    case "TOGGLE_PROJECT_OVERVIEW_SIDEBAR":
      return {
        stateKey: "projectOverviewSidebarCollapsed",
        lsKey: "project_overview_sidebar_collapsed",
      };
    default:
      // Should never happen, but TS wants a fallback
      return { stateKey: "sidebarCollapsed", lsKey: "app_sidebar_collapsed" };
  }
}

//
// 5) Our reducer: exactly how MobX’s “toggleX” acted.  If payload is undefined,
//    flip `!state[stateKey]`. Otherwise set it to payload.  Then write to localStorage
//
function themeReducer(state: ThemeState, action: ThemeAction): ThemeState {
  const { stateKey, lsKey } = lookupKey(action.type);

  // current value could be true, false, or undefined
  const current = state[stateKey];

  // decide new value
  const newVal =
    action.payload === undefined
      ? !current // !undefined → true; !true → false; !false → true
      : action.payload; // if the caller passed a boolean, use it

  // write to localStorage as a string
  try {
    localStorage.setItem(lsKey, newVal.toString());
  } catch {
    // if SSR or storage not available, just skip
  }

  return {
    ...state,
    [stateKey]: newVal,
  };
}

//
// 6) A lazy‐initializer that runs once (on mount) to hydrate from localStorage.
//    If a key is present, parse it as a boolean; otherwise leave it undefined.
//
function initThemeState(): ThemeState {
  // If there's no window or localStorage, return all undefined
  if (typeof window === "undefined") {
    return { ...initialThemeState };
  }

  const partial: Partial<ThemeState> = {};

  // For each flag, try to read localStorage; if it exists, parse "true"/"false" → boolean
  const tryRead = (lsKey: string): boolean | undefined => {
    const raw = localStorage.getItem(lsKey);
    if (raw === "true") return true;
    if (raw === "false") return false;
    return undefined;
  };

  partial.sidebarCollapsed = tryRead("app_sidebar_collapsed");
  partial.extendedSidebarCollapsed = tryRead("extended_sidebar_collapsed");
  partial.extendedProjectSidebarCollapsed = tryRead("extended_project_sidebar_collapsed");
  partial.profileSidebarCollapsed = tryRead("profile_sidebar_collapsed");
  partial.workspaceAnalyticsSidebarCollapsed = tryRead("workspace_analytics_sidebar_collapsed");
  partial.issueDetailSidebarCollapsed = tryRead("issue_detail_sidebar_collapsed");
  partial.epicDetailSidebarCollapsed = tryRead("epic_detail_sidebar_collapsed");
  partial.initiativesSidebarCollapsed = tryRead("initiatives_sidebar_collapsed");
  partial.projectOverviewSidebarCollapsed = tryRead("project_overview_sidebar_collapsed");

  // Spread over the defaults (undefined) so any missing keys stay undefined
  return {
    ...initialThemeState,
    ...partial,
  };
}

//
// 7) Finally, create the Context + Provider
//
const ThemeContext = createContext<{
  state: ThemeState;
  dispatch: React.Dispatch<ThemeAction>;
} | null>(null);

export const ThemeProvider = ({ children }: { children: ReactNode }) => {
  // useReducer with a lazy initializer (third argument)
  const [state, dispatch] = useReducer(
    themeReducer,
    initialThemeState,
    // The “initializer” runs ONCE (first render) to pull from localStorage:
    () => initThemeState()
  );

  // If you also want to write a “theme_initialized” flag (like you showed earlier),
  // you can add a useEffect here to set it once. But since initThemeState already
  // reads all existing keys, you don’t strictly need that. Omit if you don’t care.
  // useEffect(() => {
  //   if (!localStorage.getItem("theme_initialized")) {
  //     localStorage.setItem("theme_initialized", "true");
  //   }
  // }, []);

  // ⑧ Memoize so that “value” only changes when `state` really changes
  const value = useMemo(() => ({ state, dispatch }), [state]);

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
};

export default ThemeContext;
