import DayCard from "./DayCard.jsx";

function ItineraryView({ itinerary, rawJson }) {
  if (!itinerary) {
    return null;
  }

  const days = Array.isArray(itinerary.days) ? itinerary.days : [];
  const vacationName = itinerary.vacationName || "Untitled vacation";

  return (
    <section className="itinerary-view">
      <h2>Generated Itinerary</h2>
      <p className="itinerary-summary">
        <strong>{vacationName}</strong>
        {itinerary.vacationId != null && (
          <span> (Vacation ID: {itinerary.vacationId})</span>
        )}
      </p>

      {days.length === 0 ? (
        <p className="empty-message">No days found in the itinerary response.</p>
      ) : (
        <div className="day-list">
          {days.map((day, index) => (
            <DayCard key={day?.vacationDayId ?? `day-${index}`} day={day} />
          ))}
        </div>
      )}

      <details className="debug-section">
        <summary>Raw JSON response (debug)</summary>
        <pre>{rawJson}</pre>
      </details>
    </section>
  );
}

export default ItineraryView;
