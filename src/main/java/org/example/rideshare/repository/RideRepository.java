package org.example.rideshare.repository;

import org.example.rideshare.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RideRepository extends MongoRepository<Ride, String> {

    List<Ride> findByUserId(String userId);

    List<Ride> findByDriverId(String driverId);

    List<Ride> findByStatus(org.example.rideshare.model.RideStatus status);
}
