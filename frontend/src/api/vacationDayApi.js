import { API_BASE_URL, apiRequest } from "./apiClient.js";

export function buildCreateVacationDayPayload({
  dayNumber,
  date,
  dayType,
  hotelPlaceName,
}) {
  return {
    dayNumber,
    date,
    dayType,
    hotelPlaceName,
  };
}

export function buildUpdateVacationDayPayload({ dayType, hotelPlaceName }) {
  return {
    dayType,
    hotelPlaceName,
  };
}

export async function getVacationDays(vacationId, username, password) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}/days`;
  const days = await apiRequest(url, { method: "GET" }, username, password);
  return Array.isArray(days) ? days : [];
}

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

export async function updateVacationDay(
  vacationId,
  vacationDayId,
  requestBody,
  username,
  password
) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}/days/${vacationDayId}`;
  return apiRequest(
    url,
    {
      method: "PUT",
      body: JSON.stringify(requestBody),
    },
    username,
    password
  );
}
