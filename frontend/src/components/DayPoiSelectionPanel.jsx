const DAY_TYPES = ["DAY", "NIGHT", "HALF_DAY"];

function DayPoiSelectionPanel({
  selectedDay,
  dayForm,
  onDayFormChange,
  onSaveDay,
  savingDay,
  saveError,
  saveSuccess,
  isSavedDay,
}) {
  if (!selectedDay) {
    return (
      <section className="day-poi-selection-panel">
        <h3>Day details</h3>
        <p className="empty-message">Select a vacation day to configure POIs.</p>
      </section>
    );
  }

  return (
    <section className="day-poi-selection-panel">
      <h3>
        Day {selectedDay.dayNumber} — {selectedDay.date}
      </h3>

      <div className="day-form-grid">
        <div className="form-field">
          <label htmlFor="dayType">Day type</label>
          <select
            id="dayType"
            value={dayForm.dayType}
            onChange={(event) =>
              onDayFormChange({ ...dayForm, dayType: event.target.value })
            }
            disabled={savingDay}
          >
            {DAY_TYPES.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
        </div>

        <div className="form-field form-field-wide">
          <label htmlFor="hotelPlaceName">Hotel place name</label>
          <input
            id="hotelPlaceName"
            type="text"
            placeholder="e.g. Hotel Roma"
            value={dayForm.hotelPlaceName}
            onChange={(event) =>
              onDayFormChange({
                ...dayForm,
                hotelPlaceName: event.target.value,
              })
            }
            disabled={savingDay}
          />
        </div>
      </div>

      <button
        type="button"
        className="primary-button"
        onClick={onSaveDay}
        disabled={savingDay}
      >
        {savingDay
          ? "Saving..."
          : isSavedDay
            ? "Update day activities"
            : "Save day activities"}
      </button>

      {saveError && <p className="error-message">{saveError}</p>}
      {saveSuccess && <p className="success-message">{saveSuccess}</p>}
    </section>
  );
}

export default DayPoiSelectionPanel;
