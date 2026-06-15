function formatValue(value) {
  if (value === null || value === undefined || value === "") {
    return "—";
  }
  return String(value);
}

function formatBudget(budget) {
  if (budget === null || budget === undefined || budget === "") {
    return "—";
  }
  return `₪${budget}`;
}

function VacationDetails({ vacation }) {
  if (!vacation) {
    return null;
  }

  const dateRange = (
    <span className="summary-date-range">
      {formatValue(vacation.startDate)} → {formatValue(vacation.endDate)}
    </span>
  );

  return (
    <section className="vacation-details-banner">
      <h2 className="vacation-details-title">Active vacation</h2>

      <div className="vacation-summary-compact">
        <p className="vacation-summary-line">
          <span className="summary-chunk">#{formatValue(vacation.id)}</span>
          <span className="summary-separator">·</span>
          <span className="summary-chunk summary-name">
            {formatValue(vacation.name)}
          </span>
          <span className="summary-separator">·</span>
          <span className="summary-chunk">
            {formatValue(vacation.city)}, {formatValue(vacation.country)}
          </span>
        </p>

        <p className="vacation-summary-line">
          {dateRange}
          <span className="summary-separator">·</span>
          <span className="summary-chunk">{formatValue(vacation.travelerType)}</span>
          <span className="summary-separator">·</span>
          <span className="summary-chunk">{formatBudget(vacation.budget)}</span>
          <span className="summary-separator">·</span>
          <span className="summary-chunk">{formatValue(vacation.pace)}</span>
        </p>
      </div>
    </section>
  );
}

export default VacationDetails;
