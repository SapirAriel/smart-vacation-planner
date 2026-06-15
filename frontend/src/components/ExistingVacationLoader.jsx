import { useState } from "react";
import { getVacationById } from "../api/vacationApi.js";

function ExistingVacationLoader({
  username,
  password,
  onVacationLoaded,
  loading,
}) {
  const [expanded, setExpanded] = useState(false);
  const [vacationId, setVacationId] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  async function handleLoad(event) {
    event.preventDefault();

    const trimmedId = vacationId.trim();
    if (!trimmedId) {
      setError("Please enter a vacation ID to load.");
      return;
    }

    setSubmitting(true);
    setError("");

    try {
      const vacation = await getVacationById(trimmedId, username, password);
      onVacationLoaded(vacation);
    } catch (requestError) {
      const message =
        requestError instanceof Error
          ? requestError.message
          : "Something went wrong while loading the vacation.";
      setError(message);
    } finally {
      setSubmitting(false);
    }
  }

  const isDisabled = submitting || loading;

  return (
    <div className="existing-vacation-loader">
      <button
        type="button"
        className="text-button"
        onClick={() => setExpanded((current) => !current)}
        aria-expanded={expanded}
      >
        {expanded
          ? "Hide load existing vacation"
          : "Load existing vacation for testing"}
      </button>

      {expanded && (
        <div className="existing-vacation-loader-panel">
          <p className="section-hint">
            Load an existing vacation by ID and continue the same planning flow.
          </p>

          <form className="inline-form" onSubmit={handleLoad}>
            <div className="form-field">
              <label htmlFor="existingVacationId">Vacation ID</label>
              <input
                id="existingVacationId"
                type="number"
                min="1"
                value={vacationId}
                onChange={(event) => setVacationId(event.target.value)}
                disabled={isDisabled}
              />
            </div>

            <button
              type="submit"
              className="secondary-button"
              disabled={isDisabled}
            >
              {submitting ? "Loading..." : "Load Vacation"}
            </button>
          </form>

          {error && <p className="error-message">{error}</p>}
        </div>
      )}
    </div>
  );
}

export default ExistingVacationLoader;
