import { useMemo } from "react";
import { CircleMarker, MapContainer, Popup, TileLayer } from "react-leaflet";
import ClosePopupsOnDayChange from "./ClosePopupsOnDayChange.jsx";
import PoiMapPopup from "./PoiMapPopup.jsx";

const DEFAULT_CENTER = [41.9028, 12.4964];
const DEFAULT_ZOOM = 12;

const MARKER_STYLES = {
  neutral: {
    radius: 7,
    color: "#6b7280",
    fillColor: "#9ca3af",
  },
  otherDay: {
    radius: 9,
    color: "#b45309",
    fillColor: "#f59e0b",
  },
  currentDay: {
    radius: 10,
    color: "#1d4ed8",
    fillColor: "#2563eb",
  },
};

function hasValidCoordinates(poi) {
  return (
    typeof poi.latitude === "number" &&
    typeof poi.longitude === "number" &&
    !Number.isNaN(poi.latitude) &&
    !Number.isNaN(poi.longitude)
  );
}

function getPoiMarkerState(poiId, selectedDayNumber, selectedPoiIdsByDay) {
  if (
    selectedDayNumber &&
    (selectedPoiIdsByDay[selectedDayNumber] || []).includes(poiId)
  ) {
    return "currentDay";
  }

  const selectedOnAnotherDay = Object.entries(selectedPoiIdsByDay).some(
    ([dayNumber, poiIds]) =>
      Number(dayNumber) !== selectedDayNumber && poiIds.includes(poiId)
  );

  if (selectedOnAnotherDay) {
    return "otherDay";
  }

  return "neutral";
}

function MapSection({
  activeVacation,
  pointsOfInterest,
  selectedDayNumber,
  selectedPoiIdsByDay,
  poiLoading,
  poiError,
  onTogglePoi,
}) {
  const currentDayPoiIds = selectedDayNumber
    ? selectedPoiIdsByDay[selectedDayNumber] || []
    : [];

  const mappablePois = useMemo(
    () => pointsOfInterest.filter(hasValidCoordinates),
    [pointsOfInterest]
  );

  const mapCenter = useMemo(() => {
    if (mappablePois.length > 0) {
      const avgLat =
        mappablePois.reduce((sum, poi) => sum + poi.latitude, 0) /
        mappablePois.length;
      const avgLng =
        mappablePois.reduce((sum, poi) => sum + poi.longitude, 0) /
        mappablePois.length;
      return [avgLat, avgLng];
    }
    return DEFAULT_CENTER;
  }, [mappablePois]);

  return (
    <div className="map-section-inner">
      <p className="section-hint">
        Click map markers to inspect POIs and add them to the selected day.
        {activeVacation
          ? ` Showing POIs for ${activeVacation.city}, ${activeVacation.country}.`
          : ""}
      </p>

      {poiLoading && <p className="status-message">Loading POIs...</p>}
      {poiError && <p className="error-message">{poiError}</p>}

      {activeVacation && pointsOfInterest.length > 0 && mappablePois.length === 0 && (
        <p className="empty-message">
          No POI coordinates are available for the current results.
        </p>
      )}

      <div className="map-legend">
        <span className="legend-item legend-neutral">Available POI</span>
        <span className="legend-item legend-current">Selected for current day</span>
        <span className="legend-item legend-other">Selected for another day</span>
      </div>

      <div className="map-container">
        <MapContainer
          key={`${mapCenter[0]}-${mapCenter[1]}-${mappablePois.length}`}
          center={mapCenter}
          zoom={DEFAULT_ZOOM}
          scrollWheelZoom={true}
        >
          <ClosePopupsOnDayChange selectedDayNumber={selectedDayNumber} />
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {mappablePois.map((poi) => {
            const markerState = getPoiMarkerState(
              poi.id,
              selectedDayNumber,
              selectedPoiIdsByDay
            );
            const style = MARKER_STYLES[markerState];
            const isSelectedForCurrentDay = currentDayPoiIds.includes(poi.id);

            return (
              <CircleMarker
                key={poi.id}
                center={[poi.latitude, poi.longitude]}
                radius={style.radius}
                pathOptions={{
                  color: style.color,
                  fillColor: style.fillColor,
                  fillOpacity: 0.9,
                  weight: 2,
                }}
              >
                <Popup>
                  <PoiMapPopup
                    poi={poi}
                    selectedDayNumber={selectedDayNumber}
                    isSelectedForCurrentDay={isSelectedForCurrentDay}
                    onTogglePoi={onTogglePoi}
                  />
                </Popup>
              </CircleMarker>
            );
          })}
        </MapContainer>
      </div>

      {selectedDayNumber ? (
        <p className="map-footer-hint">
          {currentDayPoiIds.length} POI
          {currentDayPoiIds.length === 1 ? "" : "s"} selected for day{" "}
          {selectedDayNumber}
        </p>
      ) : (
        <p className="map-footer-hint">Select a vacation day to choose POIs.</p>
      )}
    </div>
  );
}

export default MapSection;
