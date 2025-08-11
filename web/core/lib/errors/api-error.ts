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

export class HttpError extends Error {
  public status: number;
  public response: {
    statusText: string;
    url: string;
    body: any;
  };

  constructor(status: number, response: Response, body: any) {
    super(`HTTP Error ${status}: ${response.statusText}`);
    this.status = status;
    this.response = {
      statusText: response.statusText,
      url: response.url,
      body,
    };

    Object.setPrototypeOf(this, HttpError.prototype);
  }

  static fromResponse(status: number, response: Response, body: any) {
    return new HttpError(status, response, body);
  }
}
