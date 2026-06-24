import { getPoiDisplayName } from "../utils/poiDisplay.js";

function formatValue(value) {
  if (value === null || value === undefined || value === "") {
    return "—";
  }
  return String(value);
}

function PointOfInterestList({
  pointsOfInterest,
  loading,
  error,
  selectedDayNumber,
  selectedPoiIds,
  onTogglePoi,
}) {
  return (
    <details className="poi-list-collapsible">
      <summary>All POIs (debug / search)</summary>
      <p className="section-hint">
        The map is the main way to explore POIs. Use this compact list only if
        you need a quick text search view.
      </p>

      {!selectedDayNumber && (
        <p className="empty-message">
          Select a vacation day before choosing POIs.
        </p>
      )}

      {loading && <p className="status-message">Loading POIs...</p>}
      {error && <p className="error-message">{error}</p>}

      {!loading && !error && pointsOfInterest.length === 0 && (
        <p className="empty-message">
          No points of interest found for this city and country.
        </p>
      )}

      {!loading && pointsOfInterest.length > 0 && (
        <ul className="poi-compact-list">
          {pointsOfInterest.map((poi) => {
            const isSelected = selectedPoiIds.includes(poi.id);

            return (
              <li key={poi.id} className="poi-compact-item">
                <label>
                  <input
                    type="checkbox"
                    checked={isSelected}
                    disabled={!selectedDayNumber}
                    onChange={() => onTogglePoi(poi.id)}
                  />
                  <span>
                    <strong>#{formatValue(poi.id)}</strong>{" "}
                    {formatValue(getPoiDisplayName(poi))}
                  </span>
                </label>
              </li>
            );
          })}
        </ul>
      )}
    </details>
  );
}

export default PointOfInterestList;
