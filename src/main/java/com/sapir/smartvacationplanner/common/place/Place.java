package com.sapir.smartvacationplanner.common.place;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;



@Embeddable
public class Place {

    @Column(name = "place_name", nullable = false)
    private String placeName; 

    // Google Maps API place details (placeId, formattedAddress, latitude, longitude)

    @Column(name = "place_id")
    private String placeId;

    @Column(name = "formatted_address")
    private String formattedAddress;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    protected Place() {
    }

    public Place(String placeName) {
        this.placeName = placeName;
    }

    public Place(String placeName, String placeId, String formattedAddress, Double latitude, Double longitude) {
        this.placeName = placeName;
        this.placeId = placeId;
        this.formattedAddress = formattedAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }
    public String getPlaceId() {
        return placeId;
    }
    public String getFormattedAddress() {
        return formattedAddress;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }   
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Place{" +
            "placeName='" + placeName + '\'' +
            ", placeId='" + placeId + '\'' +
            ", formattedAddress='" + formattedAddress + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
