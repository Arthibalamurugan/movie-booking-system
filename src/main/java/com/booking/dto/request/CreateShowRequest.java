package com.booking.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateShowRequest {

    private Long movieId;
    private String theaterName;
    private LocalDateTime showTime;
    private Integer totalSeats;
}