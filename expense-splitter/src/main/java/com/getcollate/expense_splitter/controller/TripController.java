package com.getcollate.expense_splitter.controller;

import java.util.Map;

import com.getcollate.expense_splitter.pojo.PUTTripRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.getcollate.expense_splitter.pojo.POSTTripRequest;

@RestController()
@RequestMapping("/trip")
public class TripController {

    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    @PostMapping
    public @ResponseBody Map<String, Object> postTrip(
            @RequestBody POSTTripRequest tripRequest) {
        logger.info("Trip Name: " + tripRequest);
        return null;
    }

    @PutMapping("/{tripId}")
    public @ResponseBody Map<String, Object> putTrip(
            @RequestBody PUTTripRequest tripRequest, @PathVariable String tripId) {
        logger.info("Trip Name: " + tripRequest + " Trip Id: " + tripId);
        return null;
    }

    @GetMapping("/{tripId}/details")
    public @ResponseBody Map<String, Object> getTrip(
            @PathVariable String tripId) {
        logger.info("Trip Id: " + tripId);
        return null;
    }

    @GetMapping("/all")
    public @ResponseBody Map<String, Object> getAllTrips() {
        logger.info("Aum Sri Sai Ram");
        return null;
    }
}