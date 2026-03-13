package com.getcollate.expense_splitter.controller;

import com.getcollate.expense_splitter.pojo.PostTransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trip/{tripId}/transactions")
public class TripTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TripTransactionController.class);

    @GetMapping
    public Map<String, Object> queryTransactions(@PathVariable String tripId) {
        return null;
    }

    @DeleteMapping("/{transactionId}")
    public Map<String, Object> deleteTransaction(@PathVariable String tripId, @PathVariable String transactionId) {
        logger.info("TripId: " + tripId + " TransactionId: " + transactionId);
        return null;
    }

    @PostMapping
    public Map<String, Object> postTransaction(@PathVariable String tripId, @RequestBody PostTransactionRequest transactionRequest) {
        logger.info("TripId: " + tripId + " Request: " + transactionRequest);
        return null;
    }
}
