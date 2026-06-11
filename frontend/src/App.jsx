import { useState } from "react";
import { generateItinerary } from "./api/itineraryApi.js";
import Header from "./components/Header.jsx";
import MapSection from "./components/MapSection.jsx";
import VacationForm from "./components/VacationForm.jsx";
import VacationSummary from "./components/VacationSummary.jsx";
import ChatPanel from "./components/ChatPanel.jsx";
import ItineraryView from "./components/ItineraryView.jsx";

function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [selectedVacation, setSelectedVacation] = useState(null);
  const [vacationId, setVacationId] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [itinerary, setItinerary] = useState(null);
  const [rawJson, setRawJson] = useState("");

  function handleVacationCreated(createdVacation) {
    setSelectedVacation(createdVacation);
    setVacationId(String(createdVacation.id ?? ""));
    setItinerary(null);
    setRawJson("");
    setError("");
  }

  async function handleGenerate() {
    const trimmedId = vacationId.trim();

    if (!trimmedId) {
      setError("Please enter a vacation ID or create a vacation first.");
      setItinerary(null);
      setRawJson("");
      return;
    }

    setLoading(true);
    setError("");
    setItinerary(null);
    setRawJson("");

    try {
      const data = await generateItinerary(trimmedId, username, password);
      setItinerary(data);
      setRawJson(JSON.stringify(data, null, 2));
    } catch (requestError) {
      const message =
        requestError instanceof Error
          ? requestError.message
          : "Something went wrong while generating the itinerary.";
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="app">
      <Header
        username={username}
        onUsernameChange={setUsername}
        password={password}
        onPasswordChange={setPassword}
      />
      <main className="app-main">
        <VacationForm
          username={username}
          password={password}
          onVacationCreated={handleVacationCreated}
        />
        {selectedVacation && <VacationSummary vacation={selectedVacation} />}
        <MapSection selectedVacation={selectedVacation} />
        <ChatPanel
          vacationId={vacationId}
          onVacationIdChange={setVacationId}
          selectedVacation={selectedVacation}
          onGenerate={handleGenerate}
          loading={loading}
          error={error}
        />
        <ItineraryView itinerary={itinerary} rawJson={rawJson} />
      </main>
    </div>
  );
}

export default App;
