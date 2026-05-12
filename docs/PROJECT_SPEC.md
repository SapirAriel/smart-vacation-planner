# Smart Vacation Planner

## Goal
Backend system for planning vacations with future AI integration.

## MVP
- Create Vacation
- Add Vacation Days
- Add Activities

## Entities

### Vacation
- id
- name
- country
- city
- startDate
- endDate
- travelerType

### VacationDay
- id
- vacationId
- date
- dayNumber
- dayType

### Activity
- id
- vacationDayId
- name
- activityType
- location
- durationMinutes
- openingHours
- minimumAge
- notes

## Future Ideas
- Smart itinerary generation
- AI recommendations
- Preferences-based planning

