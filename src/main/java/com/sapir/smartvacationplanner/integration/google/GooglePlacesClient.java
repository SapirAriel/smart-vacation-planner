package com.sapir.smartvacationplanner.integration.google;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import com.sapir.smartvacationplanner.integration.google.dto.PlaceResult;
import com.sapir.smartvacationplanner.integration.google.dto.GooglePlacesTextSearchRequest;
import com.sapir.smartvacationplanner.integration.google.dto.GooglePlacesTextSearchResponse;  

@Service
public class GooglePlacesClient {

  private static final String FIELD_MASK = "places.id,places.formattedAddress,places.location";
  private final RestClient restClient;
  private final String apiKey;
  private final String textSearchUrl;

  public GooglePlacesClient(RestClient.Builder restClientBuilder,
    @Value("${google.maps.api-key}") String apiKey,
    @Value("${google.maps.places.text-search-url}") String textSearchUrl) {
    this.restClient = restClientBuilder.build();
    this.apiKey = apiKey;
    this.textSearchUrl = textSearchUrl;
  }

  public PlaceResult searchPlace(String query) {
    if (query == null || query.isBlank()) {
        throw new IllegalArgumentException("Search query must not be empty");
    }
    GooglePlacesTextSearchRequest request = new GooglePlacesTextSearchRequest(query);
    GooglePlacesTextSearchResponse response = restClient.post()
    .uri(textSearchUrl)
    .header("Content-Type", "application/json")
    .header("X-Goog-Api-Key", apiKey)
    .header("X-Goog-FieldMask", FIELD_MASK)
    .body(request)
    .retrieve()
    .body(GooglePlacesTextSearchResponse.class);

    if (response == null || response.places() == null || response.places().isEmpty()) {
      throw new IllegalArgumentException("No place found for query: " + query);
    }

    GooglePlacesTextSearchResponse.Place place = response.places().get(0);

    if (place.location() == null || place.location().latitude() == null || place.location().longitude() == null) {
      throw new IllegalArgumentException("No location found for place: " + place.id());
    }

    return new PlaceResult(place.id(), place.formattedAddress(), place.location().latitude(), place.location().longitude());
  }

}
