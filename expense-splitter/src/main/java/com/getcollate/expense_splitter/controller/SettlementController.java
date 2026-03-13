package com.getcollate.expense_splitter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trip/{tripId}/settlement")
public class SettlementController {

    private static final Logger logger = LoggerFactory.getLogger(SettlementController.class);

    @PostMapping
    public Map<String, Object> settle(@PathVariable String tripId, @RequestParam boolean simplify) {
        logger.info("TripId: " + tripId + " Simplify: " + simplify);
        return null;
    }
}
