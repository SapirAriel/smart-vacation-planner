import { useState } from "react";
import { generateItinerary } from "./api/itineraryApi.js";
import { searchPointsOfInterestByCity } from "./api/pointOfInterestApi.js";
import { createVacationDay } from "./api/vacationDayApi.js";
import {
  getVacationDayActivities,
  replaceVacationDayActivities,
} from "./api/vacationDayActivityApi.js";
import { deriveVacationDays } from "./utils/deriveVacationDays.js";
import { hasSavedDayWithPois } from "./utils/stepFlow.js";
import Header from "./components/Header.jsx";
import VacationCreateForm from "./components/VacationCreateForm.jsx";
import ExistingVacationLoader from "./components/ExistingVacationLoader.jsx";
import VacationDetails from "./components/VacationDetails.jsx";
import DaySelector from "./components/DaySelector.jsx";
import DayPoiSelectionPanel from "./components/DayPoiSelectionPanel.jsx";
import SelectedDayActivities from "./components/SelectedDayActivities.jsx";
import PointOfInterestList from "./components/PointOfInterestList.jsx";
import MapSection from "./components/MapSection.jsx";
import ItineraryView from "./components/ItineraryView.jsx";

const DEFAULT_DAY_FORM = {
  dayType: "DAY",
  hotelPlaceName: "",
};

function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [activeVacation, setActiveVacation] = useState(null);
  const [activeVacationId, setActiveVacationId] = useState("");
  const [derivedDays, setDerivedDays] = useState([]);
  const [pointsOfInterest, setPointsOfInterest] = useState([]);
  const [selectedDayNumber, setSelectedDayNumber] = useState(null);
  const [selectedPoiIdsByDay, setSelectedPoiIdsByDay] = useState({});
  const [dayFormByDayNumber, setDayFormByDayNumber] = useState({});
  const [savedDayDataByDayNumber, setSavedDayDataByDayNumber] = useState({});

  const [poiLoading, setPoiLoading] = useState(false);
  const [poiError, setPoiError] = useState("");
  const [vacationFlowLoading, setVacationFlowLoading] = useState(false);

  const [savingDay, setSavingDay] = useState(false);
  const [saveDayError, setSaveDayError] = useState("");
  const [saveDaySuccess, setSaveDaySuccess] = useState("");

  const [generatingItinerary, setGeneratingItinerary] = useState(false);
  const [itineraryError, setItineraryError] = useState("");
  const [itinerary, setItinerary] = useState(null);
  const [rawJson, setRawJson] = useState("");

  function resetPlanningState() {
    setDerivedDays([]);
    setPointsOfInterest([]);
    setSelectedDayNumber(null);
    setSelectedPoiIdsByDay({});
    setDayFormByDayNumber({});
    setSavedDayDataByDayNumber({});
    setPoiError("");
    setSaveDayError("");
    setSaveDaySuccess("");
    setItinerary(null);
    setRawJson("");
    setItineraryError("");
  }

  async function loadPointsOfInterest(city, country) {
    if (!city?.trim()) {
      setPointsOfInterest([]);
      setPoiError("Active vacation does not include a city for POI search.");
      return;
    }

    setPoiLoading(true);
    setPoiError("");

    try {
      const pois = await searchPointsOfInterestByCity(
        city.trim(),
        country?.trim() || "",
        username,
        password
      );
      setPointsOfInterest(pois);
    } catch (requestError) {
      const message =
        requestError instanceof Error
          ? requestError.message
          : "Something went wrong while searching points of interest.";
      setPointsOfInterest([]);
      setPoiError(message);
    } finally {
      setPoiLoading(false);
    }
  }

  async function activateVacation(vacation) {
    resetPlanningState();
    setActiveVacation(vacation);
    setActiveVacationId(String(vacation.id ?? ""));
    setDerivedDays(deriveVacationDays(vacation.startDate, vacation.endDate));
    setVacationFlowLoading(true);

    try {
      await loadPointsOfInterest(vacation.city, vacation.country);
    } finally {
      setVacationFlowLoading(false);
    }
  }

  function handleVacationCreated(vacation) {
    activateVacation(vacation);
  }

  function handleVacationLoaded(vacation) {
    activateVacation(vacation);
  }

  async function handleSelectDay(dayNumber) {
    setSelectedDayNumber(dayNumber);
    setSaveDayError("");
    setSaveDaySuccess("");

    const saved = savedDayDataByDayNumber[dayNumber];

    setDayFormByDayNumber((current) => ({
      ...current,
      [dayNumber]: saved
        ? {
            dayType: saved.dayType,
            hotelPlaceName: saved.hotelPlaceName,
          }
        : current[dayNumber] || { ...DEFAULT_DAY_FORM },
    }));

    if (saved?.pointOfInterestIds) {
      setSelectedPoiIdsByDay((current) => ({
        ...current,
        [dayNumber]: [...saved.pointOfInterestIds],
      }));
    }

    if (saved?.vacationDayId && activeVacationId) {
      try {
        const activities = await getVacationDayActivities(
          activeVacationId,
          saved.vacationDayId,
          username,
          password
        );
        const poiIds = activities
          .map((activity) => activity.pointOfInterestId)
          .filter((id) => id != null);

        setSelectedPoiIdsByDay((current) => ({
          ...current,
          [dayNumber]: poiIds,
        }));
      } catch {
        // Keep local saved state if refresh fails.
      }
    }
  }

  function handleDayFormChange(nextForm) {
    if (!selectedDayNumber) {
      return;
    }

    setDayFormByDayNumber((current) => ({
      ...current,
      [selectedDayNumber]: nextForm,
    }));
  }

  function handleTogglePoi(poiId) {
    if (!selectedDayNumber) {
      return;
    }

    setSelectedPoiIdsByDay((current) => {
      const currentIds = current[selectedDayNumber] || [];
      const exists = currentIds.includes(poiId);
      const nextIds = exists
        ? currentIds.filter((id) => id !== poiId)
        : [...currentIds, poiId];

      return {
        ...current,
        [selectedDayNumber]: nextIds,
      };
    });
  }

  async function handleSaveDay() {
    if (!activeVacation || !activeVacationId) {
      setSaveDayError("Create or load a vacation before saving a day.");
      return;
    }

    if (!selectedDayNumber) {
      setSaveDayError("Select a vacation day first.");
      return;
    }

    const selectedDay = derivedDays.find(
      (day) => day.dayNumber === selectedDayNumber
    );
    const dayForm = dayFormByDayNumber[selectedDayNumber] || {
      ...DEFAULT_DAY_FORM,
    };
    const selectedPoiIds = selectedPoiIdsByDay[selectedDayNumber] || [];

    if (!dayForm.dayType) {
      setSaveDayError("Please select a day type.");
      return;
    }

    if (!dayForm.hotelPlaceName.trim()) {
      setSaveDayError("Hotel place name is required.");
      return;
    }

    if (selectedPoiIds.length === 0) {
      setSaveDayError("Select at least one POI for this day.");
      return;
    }

    setSavingDay(true);
    setSaveDayError("");
    setSaveDaySuccess("");

    try {
      const existingSavedDay = savedDayDataByDayNumber[selectedDayNumber];
      let vacationDayId = existingSavedDay?.vacationDayId;

      if (!vacationDayId) {
        const createdDay = await createVacationDay(
          activeVacationId,
          {
            date: selectedDay.date,
            dayNumber: selectedDay.dayNumber,
            dayType: dayForm.dayType,
            hotelPlaceName: dayForm.hotelPlaceName.trim(),
          },
          username,
          password
        );
        vacationDayId = createdDay.id;
      }

      await replaceVacationDayActivities(
        activeVacationId,
        vacationDayId,
        selectedPoiIds,
        username,
        password
      );

      setSavedDayDataByDayNumber((current) => ({
        ...current,
        [selectedDayNumber]: {
          vacationDayId,
          date: selectedDay.date,
          dayNumber: selectedDay.dayNumber,
          dayType: dayForm.dayType,
          hotelPlaceName: dayForm.hotelPlaceName.trim(),
          pointOfInterestIds: [...selectedPoiIds],
        },
      }));

      setSaveDaySuccess(
        existingSavedDay?.vacationDayId
          ? `Day ${selectedDayNumber} updated successfully.`
          : `Day ${selectedDayNumber} saved successfully.`
      );
    } catch (requestError) {
      const message =
        requestError instanceof Error
          ? requestError.message
          : "Something went wrong while saving the day.";
      setSaveDayError(message);
    } finally {
      setSavingDay(false);
    }
  }

  async function handleGenerateItinerary() {
    if (!activeVacationId) {
      setItineraryError("Create or load a vacation before generating an itinerary.");
      setItinerary(null);
      setRawJson("");
      return;
    }

    if (!hasSavedDayWithPois(savedDayDataByDayNumber)) {
      setItineraryError(
        "Save at least one vacation day with selected POIs before generating an itinerary."
      );
      setItinerary(null);
      setRawJson("");
      return;
    }

    setGeneratingItinerary(true);
    setItineraryError("");
    setItinerary(null);
    setRawJson("");

    try {
      const data = await generateItinerary(
        activeVacationId,
        username,
        password
      );
      setItinerary(data);
      setRawJson(JSON.stringify(data, null, 2));
    } catch (requestError) {
      const message =
        requestError instanceof Error
          ? requestError.message
          : "Something went wrong while generating the itinerary.";
      setItineraryError(message);
    } finally {
      setGeneratingItinerary(false);
    }
  }

  const selectedDay = derivedDays.find(
    (day) => day.dayNumber === selectedDayNumber
  );
  const currentDayForm =
    (selectedDayNumber && dayFormByDayNumber[selectedDayNumber]) ||
    DEFAULT_DAY_FORM;
  const currentSelectedPoiIds = selectedDayNumber
    ? selectedPoiIdsByDay[selectedDayNumber] || []
    : [];
  const canGenerateItinerary = hasSavedDayWithPois(savedDayDataByDayNumber);

  return (
    <div className="app">
      <Header
        username={username}
        onUsernameChange={setUsername}
        password={password}
        onPasswordChange={setPassword}
      />

      <main className="app-main">
        <VacationCreateForm
          username={username}
          password={password}
          onVacationCreated={handleVacationCreated}
          loading={vacationFlowLoading}
        />

        <ExistingVacationLoader
          username={username}
          password={password}
          onVacationLoaded={handleVacationLoaded}
          loading={vacationFlowLoading}
        />

        <VacationDetails vacation={activeVacation} />

        <section className="step-section step-2-section">
          <h2>Step 2: Choose POIs for each vacation day</h2>
          <p className="section-hint">
            Select a vacation day, then choose POIs from the map and save the day.
          </p>

          {!activeVacation ? (
            <p className="step-locked-message">
              Create or load a vacation first.
            </p>
          ) : (
            <div className="planner-grid">
              <div className="planner-left">
                <DaySelector
                  derivedDays={derivedDays}
                  selectedDayNumber={selectedDayNumber}
                  selectedPoiIdsByDay={selectedPoiIdsByDay}
                  savedDayDataByDayNumber={savedDayDataByDayNumber}
                  onSelectDay={handleSelectDay}
                />

                <DayPoiSelectionPanel
                  selectedDay={selectedDay}
                  dayForm={currentDayForm}
                  onDayFormChange={handleDayFormChange}
                  onSaveDay={handleSaveDay}
                  savingDay={savingDay}
                  saveError={saveDayError}
                  saveSuccess={saveDaySuccess}
                  isSavedDay={Boolean(savedDayDataByDayNumber[selectedDayNumber])}
                />

                <SelectedDayActivities
                  selectedDayNumber={selectedDayNumber}
                  pointsOfInterest={pointsOfInterest}
                  selectedPoiIds={currentSelectedPoiIds}
                />

                <PointOfInterestList
                  pointsOfInterest={pointsOfInterest}
                  loading={poiLoading}
                  error={poiError}
                  selectedDayNumber={selectedDayNumber}
                  selectedPoiIds={currentSelectedPoiIds}
                  onTogglePoi={handleTogglePoi}
                />
              </div>

              <div className="planner-right">
                <MapSection
                  activeVacation={activeVacation}
                  pointsOfInterest={pointsOfInterest}
                  selectedDayNumber={selectedDayNumber}
                  selectedPoiIdsByDay={selectedPoiIdsByDay}
                  poiLoading={poiLoading}
                  poiError={poiError}
                  onTogglePoi={handleTogglePoi}
                />
              </div>
            </div>
          )}
        </section>

        <section
          className={`step-section itinerary-step-section${!canGenerateItinerary ? " step-locked" : ""}`}
        >
          <h2>Step 3: Generate itinerary</h2>

          {!activeVacation ? (
            <p className="step-locked-message">
              Create or load a vacation first.
            </p>
          ) : !canGenerateItinerary ? (
            <>
              <p className="step-locked-message">
                Save at least one vacation day with selected POIs before
                generating an itinerary.
              </p>
              <button
                type="button"
                className="primary-button"
                disabled={true}
              >
                Generate Itinerary
              </button>
            </>
          ) : (
            <>
              <p className="section-hint">
                After saving the relevant vacation days and POIs, generate the
                full itinerary.
              </p>

              <button
                type="button"
                className="primary-button"
                onClick={handleGenerateItinerary}
                disabled={generatingItinerary}
              >
                {generatingItinerary ? "Generating..." : "Generate Itinerary"}
              </button>
            </>
          )}

          {itineraryError && (
            <p className="error-message">{itineraryError}</p>
          )}
        </section>

        <ItineraryView itinerary={itinerary} rawJson={rawJson} />
      </main>
    </div>
  );
}

export default App;
