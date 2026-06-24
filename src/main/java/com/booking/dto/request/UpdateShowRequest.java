package com.booking.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateShowRequest {

    private String theaterName;
    private LocalDateTime showTime;
}