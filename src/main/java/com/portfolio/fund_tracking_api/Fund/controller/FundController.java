package com.portfolio.fund_tracking_api.Fund.controller;

import com.portfolio.fund_tracking_api.Fund.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidAttributeValueException;

@RestController
@RequestMapping("/api/fund")
public class FundController {
    @Autowired public FundService service;

    @GetMapping("/{fundName}")
    public ResponseEntity<?> getComplete(@PathVariable String fundName) {
        try {
            return ResponseEntity.ok(service.getComplete(fundName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/holdings/{fundName}")
    public ResponseEntity<?> getHoldings(@PathVariable String fundName) {
        try {
            return ResponseEntity.ok(service.getHoldings(fundName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/breakdown/{fundName}")
    public ResponseEntity<?> getBreakdown(@PathVariable String fundName) {
        try {
            return ResponseEntity.ok(service.getBreakdown(fundName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }
}
