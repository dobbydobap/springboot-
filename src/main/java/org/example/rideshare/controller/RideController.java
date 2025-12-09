package org.example.rideshare.controller;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.model.Ride;
import org.example.rideshare.model.RideStatus;
import org.example.rideshare.service.RideService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RideController {

    private final RideService service;

    public RideController(RideService s) {
        this.service = s;
    }

    // --- Original Assignment 1 Endpoints ---

    @PostMapping("/rides")
    public Ride createRide(@AuthenticationPrincipal UserDetails user,
            @RequestBody CreateRideRequest request) { // Changed to DTO to match Service signature
        return service.requestRide(request, user.getUsername());
    }

    @PostMapping("/rides/accept/{id}")
    public Ride accept(@AuthenticationPrincipal UserDetails driver,
            @PathVariable String id) {
        return service.acceptRide(id, driver.getUsername());
    }

    @PostMapping("/rides/complete/{id}")
    public Ride complete(@AuthenticationPrincipal UserDetails user, // Can be user or driver
            @PathVariable String id) {
        return service.completeRide(id, user.getUsername());
    }

    @GetMapping("/rides/driver/me")
    public List<Ride> getMyDriverRides(@AuthenticationPrincipal UserDetails driver) {
        return service.getDriverRides(driver.getUsername());
    }

    @GetMapping("/rides/user/me")
    public List<Ride> getMyUserRides(@AuthenticationPrincipal UserDetails user) {
        return service.getUserRides(user.getUsername());
    }

    // --- Assignment 2 Endpoints (v1) ---

    // 1. Search rides by keyword
    @GetMapping("/v1/rides/search")
    public List<Ride> search(@RequestParam("text") String text) {
        return service.searchRides(text);
    }

    // 2. Filter by distance
    @GetMapping("/v1/rides/filter-distance")
    public List<Ride> filterDistance(@RequestParam Double min, @RequestParam Double max) {
        return service.filterByDistance(min, max);
    }

    // 3. Filter by date range
    @GetMapping("/v1/rides/filter-date-range")
    public List<Ride> filterDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return service.filterByDateRange(start, end);
    }

    // 4. Sort by fare
    @GetMapping("/v1/rides/sort")
    public List<Ride> sort(@RequestParam String order) {
        return service.sortByFare(order);
    }

    // 5. Get rides for user
    @GetMapping("/v1/rides/user/{userId}")
    public List<Ride> getUserRidesById(@PathVariable String userId) {
        return service.getRidesByUserId(userId);
    }

    // 6. Get rides for user by status
    @GetMapping("/v1/rides/user/{userId}/status/{status}")
    public List<Ride> getUserRidesByStatus(@PathVariable String userId, @PathVariable RideStatus status) {
        return service.getRidesByUserAndStatus(userId, status);
    }

    // 7. Driver's active rides
    @GetMapping("/v1/driver/{driverId}/active-rides")
    public List<Ride> getDriverActiveRides(@PathVariable String driverId) {
        return service.getDriverActiveRides(driverId);
    }

    // 8. Filter rides by status + keyword
    @GetMapping("/v1/rides/filter-status")
    public List<Ride> filterStatus(@RequestParam RideStatus status, @RequestParam("search") String search) {
        return service.filterByStatusAndKeyword(status, search);
    }

    // 9. Advanced search
    @GetMapping("/v1/rides/advanced-search")
    public List<Ride> advancedSearch(@RequestParam(required = false) String search,
            @RequestParam(required = false) RideStatus status,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.advancedSearch(search, status, sort, order, page, size);
    }

    // 14. Rides on specific date
    @GetMapping("/v1/rides/date/{date}")
    public List<Ride> ridesByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getRidesByDate(date);
    }
}
