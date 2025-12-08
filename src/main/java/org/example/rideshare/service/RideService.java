package org.example.rideshare.service;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.exception.BadRequestException;
import org.example.rideshare.exception.NotFoundException;
import org.example.rideshare.model.Ride;
import org.example.rideshare.model.RideStatus;
import org.example.rideshare.model.User;
import org.example.rideshare.repository.RideRepository;
import org.example.rideshare.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideService(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    // 🚕 Request a ride (Passenger)
    public Ride requestRide(CreateRideRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!"ROLE_USER".equals(user.getRole())) {
            throw new AccessDeniedException("Only passengers (ROLE_USER) can request rides");
        }

        Ride ride = new Ride();
        ride.setUserId(user.getId());
        ride.setDriverId(null);
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropLocation(request.getDropLocation());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setCreatedAt(new Date());

        return rideRepository.save(ride);
    }

    // 🚗 Get all pending ride requests (for drivers)
    public List<Ride> getPendingRides() {
        return rideRepository.findByStatus(RideStatus.REQUESTED);
    }

    // ✔ Driver accepts a ride
    public Ride acceptRide(String rideId, String driverUsername) {
        User driver = userRepository.findByUsername(driverUsername)
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        if (!"ROLE_DRIVER".equals(driver.getRole())) {
            throw new AccessDeniedException("Only drivers (ROLE_DRIVER) can accept rides");
        }

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new BadRequestException("Ride is not in REQUESTED status");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus(RideStatus.ACCEPTED);

        return rideRepository.save(ride);
    }

    // ✔ Complete a ride (Driver or User)
    public Ride completeRide(String rideId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new BadRequestException("Ride must be in ACCEPTED status to complete");
        }

        // Check if user is the passenger or driver of this ride
        boolean isPassenger = user.getId().equals(ride.getUserId());
        boolean isDriver = user.getId().equals(ride.getDriverId());

        if (!isPassenger && !isDriver) {
            throw new AccessDeniedException("You are not authorized to complete this ride");
        }

        ride.setStatus(RideStatus.COMPLETED);
        return rideRepository.save(ride);
    }

    // ✔ Get user's own rides
    public List<Ride> getUserRides(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return rideRepository.findByUserId(user.getId());
    }

    // ✔ Get driver's rides
    public List<Ride> getDriverRides(String username) {
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        return rideRepository.findByDriverId(driver.getId());
    }
}
