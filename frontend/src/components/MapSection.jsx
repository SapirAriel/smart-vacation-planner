import { MapContainer, TileLayer } from "react-leaflet";

const ROME_CENTER = [41.9028, 12.4964];
const DEFAULT_ZOOM = 12;

function MapSection() {
  return (
    <section className="map-section">
      <h2>Map</h2>
      <p className="section-hint">
        Activity markers and routes will appear here in a future phase.
      </p>
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
