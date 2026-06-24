package com.booking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsResponse {

    private long totalUsers;
    private long totalMovies;
    private long totalShows;
    private long totalBookings;
    private double occupancyRate;
}