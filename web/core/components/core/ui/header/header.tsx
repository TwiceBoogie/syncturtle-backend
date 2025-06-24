"use client";

import * as React from "react";
import { cn } from "@/helpers/common.helper";
import { EHeaderVariant, getHeaderStyle, THeaderVariant } from "./helper";
import { ERowVariant, Row } from "../row";

// Props interface for Header component
export interface HeaderProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: THeaderVariant; // Header style variant
  setHeight?: boolean; // Control min-height application
  className?: string; // Additional custom classes
  children: React.ReactNode; // Header content
  showOnMobile?: boolean; // Mobile visibility for secondary variant
}

// Context to share variant with sub-components
const HeaderContext = React.createContext<THeaderVariant | null>(null);

/**
 * Flexible Header component with multiple style variants
 * - Provides context to child components
 * - Handles responsive visibility
 * - Manages different style configurations
 */
const Header = (props: HeaderProps) => {
  const {
    variant = EHeaderVariant.PRIMARY, // Default to primary variant
    className = "",
    showOnMobile = true, // Default visible on mobile
    setHeight = true, // Default apply min-height
    children,
    ...rest // Capture other HTML div props
  } = props;

  // Get complete style string based on props
  const style = getHeaderStyle(variant, setHeight, showOnMobile);

  return (
    // Provide variant to child components via context
    <HeaderContext.Provider value={variant}>
      {/* 
        Use Row component as base container
        - PRIMARY variant uses HUGGING row (no padding)
        - Others use REGULAR row
      */}
      <Row
        variant={variant === EHeaderVariant.PRIMARY ? ERowVariant.HUGGING : ERowVariant.REGULAR}
        className={cn(style, className)} // Merge all classes
        {...rest} // Pass other HTML attributes
      >
        {children}
      </Row>
    </HeaderContext.Provider>
  );
};

/**
 * Left-aligned header content area
 * - Handles text truncation
 * - Manages item spacing
 */
const LeftItem = (props: HeaderProps) => (
  <div
    className={cn("flex flex-wrap items-center gap-2 overflow-ellipsis whitespace-nowrap max-w-[80%]", props.className)}
  >
    {props.children}
  </div>
);

/**
 * Right-aligned header content area
 * - Uses context to adjust alignment based on header variant
 * - Throws error if used outside Header context
 */
const RightItem = (props: HeaderProps) => {
  const variant = React.useContext(HeaderContext);
  if (variant === undefined) throw new Error("RightItem must be used within Header");
  return (
    <div
      className={cn(
        "flex justify-end gap-3 w-auto items-start",
        {
          "items-baseline": variant === EHeaderVariant.TERNARY,
        },
        props.className
      )}
    >
      {props.children}
    </div>
  );
};

Header.LeftItem = LeftItem;
Header.RightItem = RightItem;
Header.displayName = "plane-ui-header";

export { Header, EHeaderVariant };
