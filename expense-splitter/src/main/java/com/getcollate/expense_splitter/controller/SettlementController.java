package com.getcollate.expense_splitter.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trip/{tripId}/settlement")
public class SettlementController {

    @PostMapping
    public Map<String, Object> settle(@PathVariable String tripId, @RequestParam boolean simplify) {
        System.out.println("TripId: " + tripId + " Simplify: " + simplify);
        return null;
    }
}
