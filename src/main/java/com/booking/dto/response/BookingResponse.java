// response/BookingResponse.java
package com.booking.dto.response;

import com.booking.enums.BookingStatus;
import com.booking.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class BookingResponse {
    private Long bookingId;
    private String movieTitle;
    private String theaterName;
    private LocalDateTime showTime;
    private String seatNumber;
    private BigDecimal amountPaid;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime bookedAt;
}