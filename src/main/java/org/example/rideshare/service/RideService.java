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
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public RideService(RideRepository rideRepository, UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
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

    // 1. Search rides by pickup or drop location
    public List<Ride> searchRides(String text) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("pickupLocation").regex(text, "i"),
                Criteria.where("dropLocation").regex(text, "i")));
        return mongoTemplate.find(query, Ride.class);
    }

    // 2. Filter rides by distance range
    public List<Ride> filterByDistance(Double min, Double max) {
        Query query = new Query();
        query.addCriteria(Criteria.where("distanceKm").gte(min).lte(max));
        return mongoTemplate.find(query, Ride.class);
    }

    // 3. Filter rides by date range
    public List<Ride> filterByDateRange(LocalDate start, LocalDate end) {
        Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Query query = new Query();
        query.addCriteria(Criteria.where("createdAt").gte(startDate).lt(endDate));
        return mongoTemplate.find(query, Ride.class);
    }

    // 4. Sort rides by fare
    public List<Ride> sortByFare(String order) {
        Query query = new Query();
        query.with(Sort.by("asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC, "fare"));
        return mongoTemplate.find(query, Ride.class);
    }

    // 5. Get rides for user (Already implemented as getUserRides, but adding by ID
    // version)
    public List<Ride> getRidesByUserId(String userId) {
        return rideRepository.findByUserId(userId);
    }

    // 6. Get rides for user by status
    public List<Ride> getRidesByUserAndStatus(String userId, RideStatus status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).and("status").is(status));
        return mongoTemplate.find(query, Ride.class);
    }

    // 7. Get driver's active rides
    public List<Ride> getDriverActiveRides(String driverId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("driverId").is(driverId).and("status").is(RideStatus.ACCEPTED));
        return mongoTemplate.find(query, Ride.class);
    }

    // 8. Filter rides by status + keyword
    public List<Ride> filterByStatusAndKeyword(RideStatus status, String text) {
        Query query = new Query();
        Criteria statusCriteria = Criteria.where("status").is(status);
        Criteria textCriteria = new Criteria().orOperator(
                Criteria.where("pickupLocation").regex(text, "i"),
                Criteria.where("dropLocation").regex(text, "i"));
        query.addCriteria(new Criteria().andOperator(statusCriteria, textCriteria));
        return mongoTemplate.find(query, Ride.class);
    }

    // 9. Advanced search
    public List<Ride> advancedSearch(String search, RideStatus status, String sort, String order, int page, int size) {
        Query query = new Query();

        List<Criteria> criteriaList = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("pickupLocation").regex(search, "i"),
                    Criteria.where("dropLocation").regex(search, "i")));
        }
        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        if (sort != null && !sort.isEmpty()) {
            query.with(Sort.by("asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
        }

        query.with(PageRequest.of(page, size));
        return mongoTemplate.find(query, Ride.class);
    }

    // 14. Rides by specific date
    public List<Ride> getRidesByDate(LocalDate date) {
        return filterByDateRange(date, date);
    }
}
