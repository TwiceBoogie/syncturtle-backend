export enum ERowVariant {
  REGULAR = "regular", // Standard padding
  HUGGING = "hugging", // No padding (hugs content)
}

export type TRowVariant = ERowVariant.REGULAR | ERowVariant.HUGGING;

export interface IRowProperties {
  [key: string]: string; // Maps variant keys to Tailwind classes
}

export const rowStyle: IRowProperties = {
  [ERowVariant.REGULAR]: "px-5", // Horizontal padding
  [ERowVariant.HUGGING]: "px-0", // No padding
};
