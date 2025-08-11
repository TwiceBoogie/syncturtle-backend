import { ApiError } from "@/lib/errors/api-error";

/* eslint-disable @typescript-eslint/no-explicit-any */
export abstract class APIService {
  protected baseURL: string;

  constructor(baseURL: string) {
    this.baseURL = baseURL;
  }

  private async request<T>(
    url: string,
    options: RequestInit = {},
    config: { supressRedirect?: boolean } = {}
  ): Promise<T> {
    const fullUrl = `${this.baseURL}${url}`;

    const response = await fetch(fullUrl, {
      credentials: "include", // same as axios withCredentials
      headers: {
        "Content-Type": "application/json",
        ...(options.headers || {}),
      },
      ...options,
    });

    if (!response.ok) {
      if (response.status === 401 && !config.supressRedirect) {
        const currentPath = window.location.pathname;
        window.location.replace(`/${currentPath ? `?next_path=${currentPath}` : ``}`);
      }

      const errorData = await response.json().catch(() => ({}));
      throw ApiError.fromResponse(errorData);
    }

    // Return parsed JSON
    return response.json();
  }

  protected get<T>(
    url: string = "",
    params: Record<string, any> = {},
    config: { supressRedirect?: boolean } = {}
  ): Promise<T> {
    const query = new URLSearchParams(params).toString();
    const fullUrl = query ? `${url}?${query}` : url;
    return this.request<T>(
      fullUrl,
      {
        method: "GET",
      },
      config
    );
  }

  protected post<T>(url: string, data: any = {}): Promise<T> {
    return this.request<T>(url, {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  protected put<T>(url: string, data: any = {}): Promise<T> {
    return this.request<T>(url, {
      method: "PUT",
      body: JSON.stringify(data),
    });
  }

  protected patch<T>(url: string, data: any = {}): Promise<T> {
    return this.request<T>(url, {
      method: "PATCH",
      body: JSON.stringify(data),
    });
  }

  protected delete<T>(url: string, data: any = {}): Promise<T> {
    return this.request<T>(url, {
      method: "DELETE",
      body: JSON.stringify(data),
    });
  }
}
