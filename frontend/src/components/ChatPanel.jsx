function ChatPanel({
  vacationId,
  onVacationIdChange,
  onGenerate,
  loading,
  error,
}) {
  return (
    <section className="chat-panel">
      <h2>Planning</h2>
      <p className="chat-intro">
        Phase 1: enter an existing vacation ID and generate an itinerary from
        the backend. A real chat flow will be added later.
      </p>

      <div className="chat-form">
        <div className="form-field">
          <label htmlFor="vacationId">Vacation ID</label>
          <input
            id="vacationId"
            type="number"
            min="1"
            value={vacationId}
            onChange={(event) => onVacationIdChange(event.target.value)}
            disabled={loading}
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
