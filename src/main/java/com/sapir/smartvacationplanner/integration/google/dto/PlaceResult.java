package com.sapir.smartvacationplanner.integration.google.dto;

public record PlaceResult(String placeId, String formattedAddress, Double latitude, Double longitude) {
}

