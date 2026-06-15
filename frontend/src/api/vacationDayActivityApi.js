import { API_BASE_URL, apiRequest } from "./apiClient.js";

export async function getVacationDayActivities(
  vacationId,
  vacationDayId,
  username,
  password
) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}/days/${vacationDayId}/vacationDayActivities`;
  const activities = await apiRequest(url, { method: "GET" }, username, password);
  return Array.isArray(activities) ? activities : [];
}

export async function createVacationDayActivity(
  vacationId,
  vacationDayId,
  pointOfInterestId,
  username,
  password
) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}/days/${vacationDayId}/vacationDayActivities/${pointOfInterestId}`;
  return apiRequest(url, { method: "POST" }, username, password);
}

export async function deleteVacationDayActivity(
  vacationId,
  vacationDayId,
  activityId,
  username,
  password
) {
  const url = `${API_BASE_URL}/api/v1/vacations/${vacationId}/days/${vacationDayId}/vacationDayActivities/${activityId}`;
  return apiRequest(url, { method: "DELETE" }, username, password);
}

// TODO: Replace this frontend orchestration with a backend bulk replace endpoint.
export async function replaceVacationDayActivities(
  vacationId,
  vacationDayId,
  pointOfInterestIds,
  username,
  password
) {
  const existingActivities = await getVacationDayActivities(
    vacationId,
    vacationDayId,
    username,
    password
  );

  for (const activity of existingActivities) {
    await deleteVacationDayActivity(
      vacationId,
      vacationDayId,
      activity.id,
      username,
      password
    );
  }

  for (const pointOfInterestId of pointOfInterestIds) {
    await createVacationDayActivity(
      vacationId,
      vacationDayId,
      pointOfInterestId,
      username,
      password
    );
  }
}
