function ChatPanel({
  vacationId,
  onVacationIdChange,
  selectedVacation,
  onGenerate,
  loading,
  error,
}) {
  const vacationIdDisabled = loading || Boolean(selectedVacation);

  return (
    <section className="chat-panel">
      <h2>Step 3: Generate itinerary</h2>
      <p className="chat-intro">
        Generate a day-by-day itinerary for your vacation using the backend
        planner.
      </p>

      {selectedVacation && (
        <p className="selected-vacation-note">
          Using created vacation: {selectedVacation.name} ({selectedVacation.city},{" "}
          {selectedVacation.country})
        </p>
      )}

      <div className="chat-form">
        <div className="form-field">
          <label htmlFor="vacationId">Vacation ID</label>
          <input
            id="vacationId"
            type="number"
            min="1"
            value={vacationId}
            onChange={(event) => onVacationIdChange(event.target.value)}
            disabled={vacationIdDisabled}
          />
        </div>

        <button
          type="button"
          className="primary-button"
          onClick={onGenerate}
          disabled={loading}
        >
          {loading ? "Generating..." : "Generate Itinerary"}
        </button>
      </div>

      {error && <p className="error-message">{error}</p>}
    </section>
  );
}

export default ChatPanel;
