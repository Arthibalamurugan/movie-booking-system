package com.booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RevenueResponse {

    private long totalBookings;
    private long confirmedBookings;
    private long cancelledBookings;
    private BigDecimal totalRevenue;
}