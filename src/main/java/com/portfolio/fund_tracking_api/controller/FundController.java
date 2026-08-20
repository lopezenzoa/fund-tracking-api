package com.portfolio.fund_tracking_api.controller;

import com.portfolio.fund_tracking_api.model.FundVariation;
import com.portfolio.fund_tracking_api.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/history/{fundName}/{fromDate}")
    public ResponseEntity<?> getShareValueHistory(
            @PathVariable String fundName,
            @PathVariable String fromDate
    ) {
        try {
            return ResponseEntity.ok(service.getShareValueHistory(fundName, fromDate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/variation/{fundName}/{fromDate}")
    public ResponseEntity<?> getShareValueVariation(
            @PathVariable String fundName,
            @PathVariable String fromDate
    ) {
        try {
            return ResponseEntity.ok(service.getShareValueVariation(fundName, fromDate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }
}
