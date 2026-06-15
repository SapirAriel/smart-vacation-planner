import { API_BASE_URL, apiRequest } from "./apiClient.js";

export async function generateItinerary(vacationId, username, password) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}/itineraries`;
  return apiRequest(url, { method: "POST" }, username, password);
}
