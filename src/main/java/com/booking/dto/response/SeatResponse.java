package com.booking.dto.response;

import com.booking.enums.SeatStatus;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class SeatResponse {
    private Long id;
    private String seatNumber;
    private SeatStatus status;
}