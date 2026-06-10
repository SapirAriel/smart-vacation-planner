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

function getErrorMessage(status, backendMessage) {
  if (status === 401) {
    return (
      "Unauthorized (401): Invalid or missing credentials. " +
      "Enter your username and password below for local testing."
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

export async function generateItinerary(vacationId, username, password) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}/itineraries`;

  const headers = {};
  const authHeader = buildAuthHeader(username, password);

  if (authHeader) {
    headers.Authorization = authHeader;
  }

  const response = await fetch(url, {
    method: "POST",
    headers,
  });

  if (!response.ok) {
    let backendMessage = "";

    try {
      const errorBody = await response.json();
      if (errorBody.message) {
        backendMessage = errorBody.message;
      } else if (errorBody.error) {
        backendMessage = errorBody.error;
      }
    } catch {
      // Response body was not JSON.
    }

    throw new Error(getErrorMessage(response.status, backendMessage));
  }

  return response.json();
}