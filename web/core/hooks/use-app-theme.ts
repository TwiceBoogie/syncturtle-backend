import { useCallback, useContext } from "react";
import ThemeContext from "@/lib/context/theme-context";

// Make the same signatures as your MobX IThemeStore:
export function useAppTheme() {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error("useAppTheme must be used within ThemeProvider");

  const { state, dispatch } = ctx;

  // Each of these callbacks has the same signature as your MobX methods:
  const toggleSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_SIDEBAR", payload: collapsed }),
    [dispatch]
  );
  const toggleExtendedSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_EXTENDED_SIDEBAR", payload: collapsed }),
    [dispatch]
  );
  const toggleExtendedProjectSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_EXTENDED_PROJECT_SIDEBAR", payload: collapsed }),
    [dispatch]
  );
  const toggleProfileSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_PROFILE_SIDEBAR", payload: collapsed }),
    [dispatch]
  );
  const toggleWorkspaceAnalyticsSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_WORKSPACE_ANALYTICS_SIDEBAR", payload: collapsed }),
    [dispatch]
  );
  const toggleIssueDetailSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_ISSUE_DETAIL_SIDEBAR", payload: collapsed }),
    [dispatch]
  );
  const toggleEpicDetailSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_EPIC_DETAIL_SIDEBAR", payload: collapsed }),
    [dispatch]
  );
  const toggleInitiativesSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_INITIATIVES_SIDEBAR", payload: collapsed }),
    [dispatch]
  );
  const toggleProjectOverviewSidebar = useCallback(
    (collapsed?: boolean) => dispatch({ type: "TOGGLE_PROJECT_OVERVIEW_SIDEBAR", payload: collapsed }),
    [dispatch]
  );

  return {
    // exactly the same “observable” getters as MobX:
    sidebarCollapsed: state.sidebarCollapsed,
    extendedSidebarCollapsed: state.extendedSidebarCollapsed,
    extendedProjectSidebarCollapsed: state.extendedProjectSidebarCollapsed,
    profileSidebarCollapsed: state.profileSidebarCollapsed,
    workspaceAnalyticsSidebarCollapsed: state.workspaceAnalyticsSidebarCollapsed,
    issueDetailSidebarCollapsed: state.issueDetailSidebarCollapsed,
    epicDetailSidebarCollapsed: state.epicDetailSidebarCollapsed,
    initiativesSidebarCollapsed: state.initiativesSidebarCollapsed,
    projectOverviewSidebarCollapsed: state.projectOverviewSidebarCollapsed,

    // and their “action” equivalents:
    toggleSidebar,
    toggleExtendedSidebar,
    toggleExtendedProjectSidebar,
    toggleProfileSidebar,
    toggleWorkspaceAnalyticsSidebar,
    toggleIssueDetailSidebar,
    toggleEpicDetailSidebar,
    toggleInitiativesSidebar,
    toggleProjectOverviewSidebar,
  };
}
