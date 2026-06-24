import { API_BASE_URL, apiRequest } from "./apiClient.js";

const POI_UPDATE_FIELDS = [
  "pointOfInterestCategory",
  "durationMinutes",
  "openingTime",
  "closingTime",
  "minimumAge",
  "notes",
];

export function buildCreatePointOfInterestPayload({
  pointOfInterestCategory,
  placeName,
  durationMinutes,
  openingTime,
  closingTime,
  minimumAge,
  notes,
}) {
  return {
    pointOfInterestCategory,
    placeName,
    durationMinutes,
    openingTime,
    closingTime,
    minimumAge,
    notes,
  };
}

export function buildUpdatePointOfInterestPayload(poiDetails) {
  return Object.fromEntries(
    POI_UPDATE_FIELDS.filter((field) => poiDetails[field] !== undefined).map(
      (field) => [field, poiDetails[field]]
    )
  );
}

export async function createPointOfInterest(requestBody, username, password) {
  const url = `${API_BASE_URL}/api/v1/points-of-interest`;
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

export async function updatePointOfInterest(
  poiId,
  requestBody,
  username,
  password
) {
  const url = `${API_BASE_URL}/api/v1/points-of-interest/${poiId}`;
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

export async function searchPointsOfInterestByCity(
  city,
  country,
  username,
  password
) {
  const params = new URLSearchParams({
    city,
    country,
    size: "100",
  });

  const url = `${API_BASE_URL}/api/v1/points-of-interest/search?${params.toString()}`;
  const page = await apiRequest(url, { method: "GET" }, username, password);
  return Array.isArray(page?.content) ? page.content : [];
}
