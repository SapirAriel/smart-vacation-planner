function formatDateParts(year, month, day) {
  return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
}

function parseDateString(dateString) {
  const [year, month, day] = dateString.split("-").map(Number);
  return { year, month, day };
}

function addDaysToDateString(dateString, daysToAdd) {
  const { year, month, day } = parseDateString(dateString);
  const date = new Date(year, month - 1, day + daysToAdd);
  return formatDateParts(date.getFullYear(), date.getMonth() + 1, date.getDate());
}

export function deriveVacationDays(startDate, endDate) {
  if (!startDate || !endDate || startDate > endDate) {
    return [];
  }

  const days = [];
  let dayNumber = 1;
  let currentDate = startDate;

  while (currentDate <= endDate) {
    days.push({
      dayNumber,
      date: currentDate,
    });
    dayNumber += 1;
    currentDate = addDaysToDateString(startDate, dayNumber - 1);
  }

  return days;
}
