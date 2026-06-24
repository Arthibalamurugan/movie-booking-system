package com.booking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data @Builder
public class ShowResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String theaterName;
    private LocalDateTime showTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal price;
}