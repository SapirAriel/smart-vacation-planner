export const API_BASE_URL = "http://localhost:8080";

export function buildAuthHeader(username, password) {
  const trimmedUsername = username?.trim();
  const trimmedPassword = password?.trim();

  if (!trimmedUsername || !trimmedPassword) {
    return null;
  }

  const credentials = btoa(`${trimmedUsername}:${trimmedPassword}`);
  return `Basic ${credentials}`;
}

function formatFieldErrors(fieldErrors) {
  if (!Array.isArray(fieldErrors) || fieldErrors.length === 0) {
    return "";
  }

  return fieldErrors
    .map((item) => `${item.field}: ${item.error}`)
    .join("; ");
}

export function getErrorMessage(status, errorBody) {
  const backendMessage = errorBody?.message || errorBody?.error || "";
  const fieldErrorText = formatFieldErrors(errorBody?.fieldErrors);

  if (status === 400) {
    if (fieldErrorText) {
      return `Bad request (400): ${backendMessage || "Validation failed"}. ${fieldErrorText}`;
    }
    if (backendMessage) {
      return `Bad request (400): ${backendMessage}`;
    }
    return "Bad request (400): Please check your request values.";
  }

  if (status === 401) {
    return (
      "Unauthorized (401): Invalid or missing credentials. " +
      "Enter your username and password in the header for local testing."
    );
  }

  if (status === 403) {
    if (backendMessage) {
      return `Forbidden (403): ${backendMessage}`;
    }
    return (
      "Forbidden (403): You are not allowed to perform this action. " +
      "Check that your account has the required role."
    );
  }

  if (backendMessage) {
    return backendMessage;
  }

  return `Request failed with status ${status}`;
}

export function buildHeaders(username, password, options = {}) {
  const headers = { ...options.headers };

  if (options.json) {
    headers["Content-Type"] = "application/json";
  }

  const authHeader = buildAuthHeader(username, password);
  if (authHeader) {
    headers.Authorization = authHeader;
  }

  return headers;
}

export async function apiRequest(url, options, username, password) {
  const response = await fetch(url, {
    ...options,
    headers: buildHeaders(username, password, {
      headers: options.headers,
      json: Boolean(options.body),
    }),
  });

  if (!response.ok) {
    let errorBody = null;

    try {
      errorBody = await response.json();
    } catch {
      // Response body was not JSON.
    }

    throw new Error(getErrorMessage(response.status, errorBody));
  }

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return response.json();
  }

  return null;
}
