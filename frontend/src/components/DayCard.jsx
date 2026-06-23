import ActivityCard from "./ActivityCard.jsx";
import { sortActivitiesByStartTime } from "../utils/sortItinerary.js";

function formatValue(value) {
  if (value === null || value === undefined || value === "") {
    return "—";
  }
  return String(value);
}

function DayCard({ day }) {
  if (!day || typeof day !== "object") {
    return null;
  }

  const activities = sortActivitiesByStartTime(
    Array.isArray(day.activities) ? day.activities : []
  );

  return (
    <article className="day-card">
      <header className="day-card-header">
        <h3>Day {formatValue(day.dayNumber)}</h3>
        <span className="day-date">{formatValue(day.date)}</span>
      </header>

      {activities.length === 0 ? (
        <p className="empty-message">No activities scheduled for this day.</p>
      ) : (
        <div className="activity-list">
          {activities.map((activity, index) => (
            <ActivityCard
              key={activity?.vacationDayActivityId ?? `activity-${index}`}
              activity={activity}
            />
          ))}
        </div>
      )}
    </article>
  );
}

export default DayCard;
