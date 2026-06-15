import { API_BASE_URL, apiRequest } from "./apiClient.js";

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

  const url = `${API_BASE_URL}/api/v1/pointOfInterests/search?${params.toString()}`;
  const page = await apiRequest(url, { method: "GET" }, username, password);
  return Array.isArray(page?.content) ? page.content : [];
}
