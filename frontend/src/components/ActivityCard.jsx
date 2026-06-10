function formatValue(value) {
  if (value === null || value === undefined || value === "") {
    return "—";
  }
  return String(value);
}

function ActivityCard({ activity }) {
  if (!activity || typeof activity !== "object") {
    return null;
  }

  return (
    <article className="activity-card">
      <h4>{formatValue(activity.activityName)}</h4>
      <dl className="activity-details">
        <div>
          <dt>Start</dt>
          <dd>{formatValue(activity.plannedStartTime)}</dd>
        </div>
        <div>
          <dt>End</dt>
          <dd>{formatValue(activity.plannedEndTime)}</dd>
        </div>
        <div>
          <dt>Place</dt>
          <dd>{formatValue(activity.placeName)}</dd>
        </div>
      </dl>
    </article>
  );
}

export default ActivityCard;
