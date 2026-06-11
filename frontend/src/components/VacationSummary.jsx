function formatValue(value) {
  if (value === null || value === undefined || value === "") {
    return "—";
  }
  return String(value);
}

function VacationSummary({ vacation }) {
  if (!vacation) {
    return null;
  }

  return (
    <section className="vacation-summary-section">
      <h2>Created vacation</h2>
      <dl className="vacation-summary-grid">
        <div>
          <dt>Vacation ID</dt>
          <dd>{formatValue(vacation.id)}</dd>
        </div>
        <div>
          <dt>Name</dt>
          <dd>{formatValue(vacation.name)}</dd>
        </div>
        <div>
          <dt>Location</dt>
          <dd>
            {formatValue(vacation.city)}, {formatValue(vacation.country)}
          </dd>
        </div>
        <div>
          <dt>Dates</dt>
          <dd>
            {formatValue(vacation.startDate)} to {formatValue(vacation.endDate)}
          </dd>
        </div>
        <div>
          <dt>Traveler type</dt>
          <dd>{formatValue(vacation.travelerType)}</dd>
        </div>
        <div>
          <dt>Budget</dt>
          <dd>{formatValue(vacation.budget)}</dd>
        </div>
        <div>
          <dt>Pace</dt>
          <dd>{formatValue(vacation.pace)}</dd>
        </div>
      </dl>
      <p className="next-step-hint">
        Next step: choose attractions for this vacation.
      </p>
    </section>
  );
}

export default VacationSummary;
