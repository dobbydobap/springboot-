package org.example.rideshare.controller;

import org.example.rideshare.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService analytics;

    public AnalyticsController(AnalyticsService a) {
        this.analytics = a;
    }

    // --- Assignment 1 Endpoint ---
    @GetMapping("/analytics/driver/{driver}/earnings")
    public Double earnings(@PathVariable String driver) {
        return analytics.totalEarnings(driver);
    }

    // --- Assignment 2 Endpoints (v1) ---

    // 10. Rides per day
    @GetMapping("/v1/analytics/rides-per-day")
    public List<Map> ridesPerDay() {
        return analytics.getRidesPerDay();
    }

    // 11. Driver summary
    @GetMapping("/v1/analytics/driver/{driverId}/summary")
    public Map<String, Object> driverSummary(@PathVariable String driverId) {
        return analytics.getDriverSummary(driverId);
    }

    // 12. User spending
    @GetMapping("/v1/analytics/user/{userId}/spending")
    public Map<String, Object> userSpending(@PathVariable String userId) {
        return analytics.getUserSpending(userId);
    }

    // 13. Status summary
    @GetMapping("/v1/analytics/status-summary")
    public List<Map> statusSummary() {
        return analytics.getStatusSummary();
    }
}
