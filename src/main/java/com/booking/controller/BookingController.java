package com.booking.controller;

import com.booking.dto.request.BookingRequest;
import com.booking.dto.response.ApiResponse;
import com.booking.dto.response.BookingResponse;
import com.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Book a seat — requires JWT
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> bookSeat(
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response = bookingService.bookSeat(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Booking confirmed successfully", response));
    }

    // Cancel a booking — requires JWT, only own bookings
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long bookingId) {

        BookingResponse response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(
                ApiResponse.success("Booking cancelled", response));
    }

    // Get my booking history — paginated
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getMyBookings(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success("Bookings fetched",
                        bookingService.getMyBookings(page, size)));
    }
    
    @GetMapping("/{bookingId}/ticket")
    public ResponseEntity<byte[]> downloadTicket(
            @PathVariable Long bookingId) {

        byte[] pdf =
                bookingService.downloadTicket(
                        bookingId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=ticket_"
                                + bookingId +
                                ".pdf")
                .contentType(
                        MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}