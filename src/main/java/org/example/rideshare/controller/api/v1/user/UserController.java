package org.example.rideshare.controller.api.v1.user;

import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.model.Ride;
import org.example.rideshare.service.RideService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final RideService rideService;

    public UserController(RideService rideService) {
        this.rideService = rideService;
    }

    // ✔ Get user's own rides
    @GetMapping("/rides")
    public List<RideResponse> getMyRides(Authentication authentication) {
        String username = authentication.getName();
        List<Ride> rides = rideService.getUserRides(username);
        return rides.stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
