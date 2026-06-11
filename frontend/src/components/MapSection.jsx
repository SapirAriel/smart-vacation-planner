import { MapContainer, TileLayer } from "react-leaflet";

const ROME_CENTER = [41.9028, 12.4964];
const DEFAULT_ZOOM = 12;

function MapSection({ selectedVacation }) {
  return (
    <section className="map-section">
      <h2>Step 2: Choose attractions for this vacation</h2>
      <p className="section-hint">
        Attraction selection by city will be added in the next phase.
      </p>
      {selectedVacation ? (
        <p className="section-hint map-context-hint">
          Showing future attractions for {selectedVacation.city},{" "}
          {selectedVacation.country}.
        </p>
      ) : (
        <p className="section-hint map-context-hint">
          Create a vacation first to choose attractions by city.
        </p>
      )}
      <div className="map-container">
        <MapContainer
          center={ROME_CENTER}
          zoom={DEFAULT_ZOOM}
          scrollWheelZoom={true}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
        </MapContainer>
      </div>
    </section>
  );
}

export default MapSection;
