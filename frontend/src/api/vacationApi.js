const API_BASE_URL = "http://localhost:8080";

function buildAuthHeader(username, password) {
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

function getErrorMessage(status, errorBody) {
  const backendMessage = errorBody?.message || errorBody?.error || "";
  const fieldErrorText = formatFieldErrors(errorBody?.fieldErrors);

  if (status === 400) {
    if (fieldErrorText) {
      return `Bad request (400): ${backendMessage || "Validation failed"}. ${fieldErrorText}`;
    }
    if (backendMessage) {
      return `Bad request (400): ${backendMessage}`;
    }
    return "Bad request (400): Please check the vacation form values.";
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

export async function createVacation(vacationRequest, username, password) {
  const url = `${API_BASE_URL}/api/v1/vacations`;

  const headers = {
    "Content-Type": "application/json",
  };

  const authHeader = buildAuthHeader(username, password);
  if (authHeader) {
    headers.Authorization = authHeader;
  }

  const response = await fetch(url, {
    method: "POST",
    headers,
    body: JSON.stringify(vacationRequest),
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

  return response.json();
}
