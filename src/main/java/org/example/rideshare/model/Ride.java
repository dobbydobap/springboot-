package org.example.rideshare.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    // RELATIONSHIP: Ride belongs to a Passenger (User with ROLE_USER)
    @NotBlank(message = "Passenger (userId) is required")
    private String userId;

    // RELATIONSHIP: Ride may be accepted by a Driver (User with ROLE_DRIVER)
    private String driverId; // nullable until a driver accepts

    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotBlank(message = "Drop location is required")
    private String dropLocation;

    @NotNull(message = "Ride status is required")
    private RideStatus status;

    @CreatedDate
    private Date createdAt = new Date();

    public Ride() {
    }

    public Ride(String userId, String pickupLocation, String dropLocation) {
        this.userId = userId;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.status = RideStatus.REQUESTED;
        this.createdAt = new Date();
    }

    // Getters and Setters omitted for brevity

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
