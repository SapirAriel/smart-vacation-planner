package com.sapir.smartvacationplanner.integration.google.dto;

import java.util.List;

public record GooglePlacesTextSearchResponse(List<Place> places) {

public record Place(String id, String formattedAddress, String placeName, Location location) {
}

public record Location(Double latitude, Double longitude) {
}

}
