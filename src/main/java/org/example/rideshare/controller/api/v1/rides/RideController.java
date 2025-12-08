package org.example.rideshare.controller.api.v1.rides;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.model.Ride;
import org.example.rideshare.service.RideService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    // 🚕 Request a ride (Passenger)
    @PostMapping
    public RideResponse requestRide(@Valid @RequestBody CreateRideRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        Ride ride = rideService.requestRide(request, username);
        return RideResponse.fromEntity(ride);
    }

    // ✔ Complete a ride (Driver or User)
    @PostMapping("/{rideId}/complete")
    public RideResponse completeRide(@PathVariable String rideId,
            Authentication authentication) {
        String username = authentication.getName();
        Ride ride = rideService.completeRide(rideId, username);
        return RideResponse.fromEntity(ride);
    }
}
