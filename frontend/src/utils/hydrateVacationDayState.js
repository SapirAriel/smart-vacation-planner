import { getVacationDays } from "../api/vacationDayApi.js";
import { getVacationDayActivities } from "../api/vacationDayActivityApi.js";

const DEFAULT_DAY_FORM = {
  dayType: "DAY",
  hotelPlaceName: "",
};

export async function hydrateVacationDayState(vacationId, username, password) {
  const savedDayDataByDayNumber = {};
  const dayFormByDayNumber = {};
  const selectedPoiIdsByDay = {};

  let days = [];
  try {
    days = await getVacationDays(vacationId, username, password);
  } catch {
    return { savedDayDataByDayNumber, dayFormByDayNumber, selectedPoiIdsByDay };
  }

  for (const day of days) {
    const dayNumber = day.dayNumber;
    if (dayNumber == null || day.id == null) {
      continue;
    }

    let pointOfInterestIds = [];
    try {
      const activities = await getVacationDayActivities(
        vacationId,
        day.id,
        username,
        password
      );
      pointOfInterestIds = activities
        .map((activity) => activity.pointOfInterestId)
        .filter((id) => id != null);
    } catch {
      // Keep the day as saved even if activities cannot be loaded.
    }

    const dayType = day.dayType || DEFAULT_DAY_FORM.dayType;
    const hotelPlaceName = day.hotelPlaceName || "";

    savedDayDataByDayNumber[dayNumber] = {
      vacationDayId: day.id,
      date: day.date,
      dayNumber: day.dayNumber,
      dayType,
      hotelPlaceName,
      pointOfInterestIds,
    };

    dayFormByDayNumber[dayNumber] = {
      dayType,
      hotelPlaceName,
    };

    if (pointOfInterestIds.length > 0) {
      selectedPoiIdsByDay[dayNumber] = [...pointOfInterestIds];
    }
  }

  return { savedDayDataByDayNumber, dayFormByDayNumber, selectedPoiIdsByDay };
}
