import { getPoiDisplayName } from "../utils/poiDisplay.js";

function formatValue(value) {
  if (value === null || value === undefined || value === "") {
    return "—";
  }
  return String(value);
}

function SelectedDayActivities({ selectedDayNumber, pointsOfInterest, selectedPoiIds }) {
  if (!selectedDayNumber) {
    return (
      <section className="selected-day-activities">
        <h3>Selected POIs for current day</h3>
        <p className="empty-message">Select a day, then choose POIs from the map.</p>
      </section>
    );
  }

  const poiMap = new Map(
    pointsOfInterest.map((poi) => [poi.id, poi])
  );

  const selectedPois = selectedPoiIds
    .map((id) => poiMap.get(id))
    .filter(Boolean);

  return (
    <section className="selected-day-activities">
      <h3>Selected POIs for day {selectedDayNumber}</h3>
      <p className="section-hint">Use the map to add or remove POIs.</p>

      {selectedPois.length === 0 ? (
        <p className="empty-message">No POIs selected for this day yet.</p>
      ) : (
        <ul className="selected-poi-list">
          {selectedPois.map((poi) => (
            <li key={poi.id}>
              <strong>#{formatValue(poi.id)}</strong>{" "}
              {formatValue(getPoiDisplayName(poi))}
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

export default SelectedDayActivities;
