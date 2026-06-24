export function getPoiDisplayName(poi) {
  if (!poi || typeof poi !== "object") {
    return "Unnamed place";
  }

  return (
    poi.placeName ||
    poi.place?.placeName ||
    poi.formattedAddress ||
    "Unnamed place"
  );
}
