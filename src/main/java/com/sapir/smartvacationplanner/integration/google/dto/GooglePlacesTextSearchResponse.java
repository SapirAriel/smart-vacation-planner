package com.sapir.smartvacationplanner.integration.google.dto;

import java.util.List;

public record GooglePlacesTextSearchResponse(List<Place> places) {

public record Place(String id, 
    String formattedAddress, 
    Location location, 
    List<AddressComponent> addressComponents) {
}

public record Location(Double latitude, 
    Double longitude) {
}

public record AddressComponent(String longText,
    String shortText,
    List<String> types,
    String languageCode
) {
}

}
