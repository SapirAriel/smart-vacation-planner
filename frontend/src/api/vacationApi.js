import { API_BASE_URL, apiRequest } from "./apiClient.js";

export async function createVacation(vacationRequest, username, password) {
  const url = `${API_BASE_URL}/api/v1/vacations`;
  return apiRequest(
    url,
    {
      method: "POST",
      body: JSON.stringify(vacationRequest),
    },
    username,
    password
  );
}

export async function getVacationById(vacationId, username, password) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}`;
  return apiRequest(url, { method: "GET" }, username, password);
}
