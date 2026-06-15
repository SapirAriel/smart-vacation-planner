export function hasSavedDayWithPois(savedDayDataByDayNumber) {
  return Object.values(savedDayDataByDayNumber).some((day) => {
    if (!day?.vacationDayId) {
      return false;
    }

    return (
      Array.isArray(day.pointOfInterestIds) && day.pointOfInterestIds.length > 0
    );
  });
}
