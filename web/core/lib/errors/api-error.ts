import type { ApiErrorResponse } from "@/types/api";

export class ApiError extends Error {
  public errors?: Record<string, string>;

  constructor(message: string, errors?: Record<string, string>) {
    super(message);
    this.name = "ApiError";
    this.errors = errors;

    Object.setPrototypeOf(this, ApiError.prototype);
  }

  static fromResponse(data: Partial<ApiErrorResponse>): ApiError {
    return new ApiError(data.message || "Unknown error", data.errors);
  }
}
