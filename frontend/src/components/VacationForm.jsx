import { useState } from "react";
import { createVacation } from "../api/vacationApi.js";

const TRAVELER_TYPES = [
  "INDIVIDUAL",
  "COUPLE",
  "FAMILY",
  "GROUP",
  "OTHER",
];

const PACE_OPTIONS = ["RELAXED", "BALANCED", "INTENSE"];

const DEFAULT_FORM = {
  name: "Rome Trip",
  country: "Italy",
  city: "Rome",
  startDate: "2026-06-01",
  endDate: "2026-06-05",
  travelerType: "COUPLE",
  budget: "3000",
  pace: "BALANCED",
};

function VacationForm({ username, password, onVacationCreated }) {
  const [form, setForm] = useState(DEFAULT_FORM);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function updateField(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function validateForm() {
    const requiredFields = [
      "name",
      "country",
      "city",
      "startDate",
      "endDate",
      "travelerType",
      "pace",
    ];

    for (const field of requiredFields) {
      if (!String(form[field]).trim()) {
        return `Please fill in ${field}.`;
      }
    }

    const budget = Number(form.budget);
    if (!form.budget || Number.isNaN(budget) || budget <= 0) {
      return "Budget must be greater than 0.";
    }

    return "";
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    setError("");

    const vacationRequest = {
      name: form.name.trim(),
      country: form.country.trim(),
      city: form.city.trim(),
      startDate: form.startDate,
      endDate: form.endDate,
      travelerType: form.travelerType,
      budget: Number(form.budget),
      pace: form.pace,
    };

    try {
      const createdVacation = await createVacation(
        vacationRequest,
        username,
        password
      );
      onVacationCreated(createdVacation);
    } catch (requestError) {
      const message =
        requestError instanceof Error
          ? requestError.message
          : "Something went wrong while creating the vacation.";
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="vacation-form-section">
      <h2>Step 1: Create vacation</h2>
      <p className="section-hint">
        Start by creating a vacation. The returned vacation ID will be used for
        itinerary generation.
      </p>

      <form className="vacation-form" onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="name">Name</label>
          <input
            id="name"
            type="text"
            value={form.name}
            onChange={(event) => updateField("name", event.target.value)}
            disabled={loading}
          />
        </div>

        <div className="form-field">
          <label htmlFor="country">Country</label>
          <input
            id="country"
            type="text"
            value={form.country}
            onChange={(event) => updateField("country", event.target.value)}
            disabled={loading}
          />
        </div>

        <div className="form-field">
          <label htmlFor="city">City</label>
          <input
            id="city"
            type="text"
            value={form.city}
            onChange={(event) => updateField("city", event.target.value)}
            disabled={loading}
          />
        </div>

        <div className="form-field">
          <label htmlFor="startDate">Start date</label>
          <input
            id="startDate"
            type="date"
            value={form.startDate}
            onChange={(event) => updateField("startDate", event.target.value)}
            disabled={loading}
          />
        </div>

        <div className="form-field">
          <label htmlFor="endDate">End date</label>
          <input
            id="endDate"
            type="date"
            value={form.endDate}
            onChange={(event) => updateField("endDate", event.target.value)}
            disabled={loading}
          />
        </div>

        <div className="form-field">
          <label htmlFor="travelerType">Traveler type</label>
          <select
            id="travelerType"
            value={form.travelerType}
            onChange={(event) => updateField("travelerType", event.target.value)}
            disabled={loading}
          >
            {TRAVELER_TYPES.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="budget">Budget</label>
          <input
            id="budget"
            type="number"
            min="1"
            step="1"
            value={form.budget}
            onChange={(event) => updateField("budget", event.target.value)}
            disabled={loading}
          />
        </div>

        <div className="form-field">
          <label htmlFor="pace">Pace</label>
          <select
            id="pace"
            value={form.pace}
            onChange={(event) => updateField("pace", event.target.value)}
            disabled={loading}
          >
            {PACE_OPTIONS.map((pace) => (
              <option key={pace} value={pace}>
                {pace}
              </option>
            ))}
          </select>
        </div>

        <div className="vacation-form-actions">
          <button
            type="submit"
            className="primary-button"
            disabled={loading}
          >
            {loading ? "Creating..." : "Create Vacation"}
          </button>
        </div>
      </form>

      {error && <p className="error-message">{error}</p>}
    </section>
  );
}

export default VacationForm;
