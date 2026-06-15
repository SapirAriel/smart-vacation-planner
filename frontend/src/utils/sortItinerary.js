export function sortItineraryDays(days) {
  if (!Array.isArray(days)) {
    return [];
  }

  return [...days].sort((left, right) => {
    const leftDayNumber = left?.dayNumber;
    const rightDayNumber = right?.dayNumber;

    if (leftDayNumber != null && rightDayNumber != null) {
      return leftDayNumber - rightDayNumber;
    }

    if (leftDayNumber != null) {
      return -1;
    }

    if (rightDayNumber != null) {
      return 1;
    }

    const leftDate = left?.date;
    const rightDate = right?.date;

    if (leftDate && rightDate) {
      return String(leftDate).localeCompare(String(rightDate));
    }

    return 0;
  });
}

export function sortActivitiesByStartTime(activities) {
  if (!Array.isArray(activities)) {
    return [];
  }

  return [...activities].sort((left, right) => {
    const leftStart = left?.plannedStartTime;
    const rightStart = right?.plannedStartTime;

    if (leftStart && rightStart) {
      return String(leftStart).localeCompare(String(rightStart));
    }

    if (leftStart) {
      return -1;
    }

    if (rightStart) {
      return 1;
    }

    return 0;
  });
}
