import { API_BASE_URL, apiRequest } from "./apiClient.js";

export async function createVacationDay(
  vacationId,
  requestBody,
  username,
  password
) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}/days`;
  return apiRequest(
    url,
    {
      method: "POST",
      body: JSON.stringify(requestBody),
    },
    username,
    password
  );
}
