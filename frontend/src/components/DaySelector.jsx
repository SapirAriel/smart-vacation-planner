function DaySelector({
  derivedDays,
  selectedDayNumber,
  selectedPoiIdsByDay,
  savedDayDataByDayNumber,
  onSelectDay,
}) {
  if (!derivedDays.length) {
    return (
      <section className="day-selector-section">
        <h3>Vacation days</h3>
        <p className="empty-message">Create a vacation to see day options.</p>
      </section>
    );
  }

  return (
    <section className="day-selector-section">
      <h3>Vacation days</h3>
      <p className="section-hint">Select a day to choose POIs and save it.</p>

      <div className="day-selector-list">
        {derivedDays.map((day) => {
          const isSelected = selectedDayNumber === day.dayNumber;
          const saved = savedDayDataByDayNumber[day.dayNumber];
          const selectedCount = (selectedPoiIdsByDay[day.dayNumber] || []).length;

          return (
            <button
              key={day.dayNumber}
              type="button"
              className={`day-selector-item${isSelected ? " selected" : ""}`}
              onClick={() => onSelectDay(day.dayNumber)}
            >
              <span className="day-selector-title">
                Day {day.dayNumber} — {day.date}
              </span>
              <span className="day-selector-meta">
                {saved ? "Saved" : "Not saved"} · {selectedCount} POI
                {selectedCount === 1 ? "" : "s"} selected
              </span>
            </button>
          );
        })}
      </div>
    </section>
  );
}

export default DaySelector;
