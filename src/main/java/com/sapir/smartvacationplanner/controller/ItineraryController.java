package com.sapir.smartvacationplanner.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sapir.smartvacationplanner.service.ItineraryService;
import com.sapir.smartvacationplanner.dto.itinerary.ItineraryResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/vacations/{vacationId}/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @PostMapping
    public ItineraryResponse generateItinerary(@PathVariable Integer vacationId) {
        return itineraryService.generateItinerary(vacationId);
    }

}
