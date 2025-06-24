import { useRef, useState } from "react";
import { useParams } from "next/navigation";
import { PenSquare } from "lucide-react";
// types

// components
// constants
// helpers
import { cn } from "@/helpers/common.helper";
// hooks
import { useAppTheme } from "@/hooks/use-app-theme";
// import useLocalStorage from "@/hooks/use-local-storage";
// plane web components
// import { AppSearch } from "@/plane-web/components/workspace";

export const SidebarQuickActions = () => {
  // states
  const [isDraftIssueModalOpen, setIsDraftIssueModalOpen] = useState(false);
  const [isDraftButtonOpen, setIsDraftButtonOpen] = useState(false);
  // refs
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const timeoutRef = useRef<any>();
  // router
  const { workspaceSlug: routerWorkspaceSlug } = useParams();
  const workspaceSlug = routerWorkspaceSlug?.toString();
  const disabled = true;
  // store hooks
  const { sidebarCollapsed: isSidebarCollapsed } = useAppTheme();
  // local storage

  const handleMouseEnter = () => {
    // if enter before time out clear the timeout
    timeoutRef?.current && clearTimeout(timeoutRef.current);
    setIsDraftButtonOpen(true);
  };

  const handleMouseLeave = () => {
    timeoutRef.current = setTimeout(() => {
      setIsDraftButtonOpen(false);
    }, 300);
  };

  return (
    <>
      <div
        className={cn("flex items-center justify-between gap-1 cursor-pointer", {
          "flex-col gap-0": isSidebarCollapsed,
        })}
      >
        <button
          type="button"
          className={cn(
            "relative flex flex-shrink-0 flex-grow items-center gap-2 h-8 text-custom-sidebar-text-300 rounded outline-none hover:bg-custom-sidebar-background-90",
            {
              "justify-center size-8 aspect-square": isSidebarCollapsed,
              "cursor-not-allowed opacity-50 ": disabled,
              "px-3 border-[0.5px] border-custom-sidebar-border-300": !isSidebarCollapsed,
            }
          )}
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
          disabled={disabled}
        >
          <PenSquare className="size-4" />
          {!isSidebarCollapsed && (
            <span className="text-sm font-medium truncate max-w-[145px]">
              {/* {t("sidebar.new_work_item")} */}
              sidebar stuff
            </span>
          )}
        </button>
      </div>
    </>
  );
};
