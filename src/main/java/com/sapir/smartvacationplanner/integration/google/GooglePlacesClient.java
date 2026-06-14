package com.sapir.smartvacationplanner.integration.google;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import com.sapir.smartvacationplanner.integration.google.dto.PlaceResult;
import com.sapir.smartvacationplanner.integration.google.dto.GooglePlacesTextSearchRequest;
import com.sapir.smartvacationplanner.integration.google.dto.GooglePlacesTextSearchResponse;  
import java.util.List;
import java.util.Optional;

@Service
public class GooglePlacesClient {

  private static final String FIELD_MASK = "places.id,places.formattedAddress,places.location,places.addressComponents";
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

    String city = extractCity(place.addressComponents());
    String country = extractCountry(place.addressComponents());

    return new PlaceResult(place.id(), place.formattedAddress(), place.location().latitude(), place.location().longitude(), city, country);
  }

  private String extractCountry(List<GooglePlacesTextSearchResponse.AddressComponent> components) {
    return findLongTextByType(components, "country")
            .orElse(null);
}

  private String extractCity(List<GooglePlacesTextSearchResponse.AddressComponent> components) {
      return findLongTextByType(components, "locality")
              .or(() -> findLongTextByType(components, "postal_town"))
              .or(() -> findLongTextByType(components, "administrative_area_level_2"))
              .or(() -> findLongTextByType(components, "administrative_area_level_1"))
              .orElse(null);
  }

  private Optional<String> findLongTextByType(
          List<GooglePlacesTextSearchResponse.AddressComponent> components, String type) {
      if (components == null) {
          return Optional.empty();
      }

      return components.stream()
              .filter(component -> component.types() != null)
              .filter(component -> component.types().contains(type))
              .map(GooglePlacesTextSearchResponse.AddressComponent::longText)
              .filter(text -> text != null && !text.isBlank())
              .findFirst();
}

}
