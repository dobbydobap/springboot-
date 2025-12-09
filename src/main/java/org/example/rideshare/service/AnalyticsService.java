package org.example.rideshare.service;

import org.example.rideshare.model.RideStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class AnalyticsService {

    private final MongoTemplate template;

    public AnalyticsService(MongoTemplate template) {
        this.template = template;
    }

    // Existing method from assignment 1 instructions
    public Double totalEarnings(String driver) {
        Aggregation agg = newAggregation(
                match(Criteria.where("driverId").is(driver).and("status").is(RideStatus.COMPLETED)),
                group().sum("fare").as("total"));

        Map result = template.aggregate(agg, "rides", Map.class).getUniqueMappedResult();
        return result != null && result.get("total") != null ? ((Number) result.get("total")).doubleValue() : 0.0;
    }

    // 10. Rides per day
    public List<Map> getRidesPerDay() {
        Aggregation agg = newAggregation(
                project().and(DateOperators.DateToString.dateOf("createdAt").toString("%Y-%m-%d")).as("date"),
                group("date").count().as("count"),
                sort(Sort.Direction.ASC, "_id"));
        return template.aggregate(agg, "rides", Map.class).getMappedResults();
    }

    // 11. Driver summary (completed rides, total fare, avg distance)
    public Map<String, Object> getDriverSummary(String driverId) {
        Aggregation agg = newAggregation(
                match(Criteria.where("driverId").is(driverId).and("status").is(RideStatus.COMPLETED)),
                group("driverId")
                        .count().as("completedRides")
                        .sum("fare").as("totalEarnings")
                        .avg("distanceKm").as("avgDistance"));
        return template.aggregate(agg, "rides", Map.class).getUniqueMappedResult();
    }

    // 12. User spending
    public Map<String, Object> getUserSpending(String userId) {
        Aggregation agg = newAggregation(
                match(Criteria.where("userId").is(userId).and("status").is(RideStatus.COMPLETED)),
                group("userId")
                        .count().as("totalRides")
                        .sum("fare").as("totalSpent"));
        return template.aggregate(agg, "rides", Map.class).getUniqueMappedResult();
    }

    // 13. Status summary
    public List<Map> getStatusSummary() {
        Aggregation agg = newAggregation(
                group("status").count().as("count"));
        return template.aggregate(agg, "rides", Map.class).getMappedResults();
    }
}
