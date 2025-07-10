export async function secureFetch(url: string, options: RequestInit = {}) {
  return fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      "x-api-key": process.env.INTERNAL_API_KEY!,
    },
  });
}
