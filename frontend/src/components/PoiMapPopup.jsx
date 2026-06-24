import { getPoiDisplayName } from "../utils/poiDisplay.js";

const NOTES_MAX_LENGTH = 100;

function formatValue(value) {
  if (value === null || value === undefined || value === "") {
    return null;
  }
  return String(value);
}

function buildLocationLine(poi) {
  const place = formatValue(poi.placeName);
  const city = formatValue(poi.city);
  const country = formatValue(poi.country);

  if (place && city && !place.includes(city)) {
    return `${place}, ${city}`;
  }

  if (place) {
    return place;
  }

  if (city && country) {
    return `${city}, ${country}`;
  }

  return city || country;
}

function buildMetadataLine(poi) {
  const parts = [];

  if (poi.durationMinutes != null && poi.durationMinutes !== "") {
    parts.push(`${poi.durationMinutes} min`);
  }

  const opening = formatValue(poi.openingTime);
  const closing = formatValue(poi.closingTime);

  if (opening && closing) {
    parts.push(`${opening}–${closing}`);
  } else if (opening) {
    parts.push(opening);
  }

  if (poi.minimumAge != null && poi.minimumAge !== "") {
    parts.push(`Age ${poi.minimumAge}+`);
  }

  return parts.length > 0 ? parts.join(" · ") : null;
}

function truncateNotes(notes) {
  const trimmed = notes.trim();
  if (trimmed.length <= NOTES_MAX_LENGTH) {
    return trimmed;
  }

  return `${trimmed.slice(0, NOTES_MAX_LENGTH).trimEnd()}…`;
}

function PoiMapPopup({
  poi,
  selectedDayNumber,
  isSelectedForCurrentDay,
  onTogglePoi,
}) {
  const locationLine = buildLocationLine(poi);
  const metadataLine = buildMetadataLine(poi);
  const address = formatValue(poi.formattedAddress);
  const notes = poi.notes?.trim() ? truncateNotes(poi.notes) : null;

  return (
    <div className="map-popup">
      <h3 className="map-popup-title">{getPoiDisplayName(poi)}</h3>

      <p className="map-popup-secondary">
        #{formatValue(poi.id)}
        {poi.pointOfInterestCategory
          ? ` · ${formatValue(poi.pointOfInterestCategory)}`
          : ""}
      </p>

      {locationLine && (
        <p className="map-popup-location">{locationLine}</p>
      )}

      {metadataLine && (
        <p className="map-popup-metadata">{metadataLine}</p>
      )}

      {address && <p className="map-popup-address">{address}</p>}

      {notes && <p className="map-popup-notes">{notes}</p>}

      {!selectedDayNumber ? (
        <p className="map-popup-hint">
          Select a vacation day first to choose this POI.
        </p>
      ) : (
        <button
          type="button"
          className={
            isSelectedForCurrentDay
              ? "map-popup-button map-popup-button-danger"
              : "map-popup-button map-popup-button-primary"
          }
          onClick={() => onTogglePoi(poi.id)}
        >
          {isSelectedForCurrentDay
            ? "Remove from current day"
            : "Add to current day"}
        </button>
      )}
    </div>
  );
}

export default PoiMapPopup;
