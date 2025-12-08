package org.example.rideshare.controller.api.v1.driver;

import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.model.Ride;
import org.example.rideshare.service.RideService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/driver")
@PreAuthorize("hasRole('DRIVER')")
public class DriverController {

    private final RideService rideService;

    public DriverController(RideService rideService) {
        this.rideService = rideService;
    }

    // 🚗 View all pending ride requests
    @GetMapping("/rides/requests")
    public List<RideResponse> getPendingRides() {
        List<Ride> rides = rideService.getPendingRides();
        return rides.stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ✔ Accept a ride
    @PostMapping("/rides/{rideId}/accept")
    public RideResponse acceptRide(@PathVariable String rideId,
            Authentication authentication) {
        String driverUsername = authentication.getName();
        Ride ride = rideService.acceptRide(rideId, driverUsername);
        return RideResponse.fromEntity(ride);
    }
}
