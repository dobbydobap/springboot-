package org.example.rideshare.dto;

import org.example.rideshare.model.Ride;
import org.example.rideshare.model.RideStatus;

import java.util.Date;

public class RideResponse {

    private String id;
    private String userId;
    private String driverId;
    private String pickupLocation;
    private String dropLocation;
    private RideStatus status;
    private Date createdAt;

    public static RideResponse fromEntity(Ride ride) {
        RideResponse res = new RideResponse();
        res.id = ride.getId();
        res.userId = ride.getUserId();
        res.driverId = ride.getDriverId();
        res.pickupLocation = ride.getPickupLocation();
        res.dropLocation = ride.getDropLocation();
        res.status = ride.getStatus();
        res.createdAt = ride.getCreatedAt();
        return res;
    }

    // Getters (and setters if you want)

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public RideStatus getStatus() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
