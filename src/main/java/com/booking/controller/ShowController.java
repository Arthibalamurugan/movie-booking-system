package com.booking.controller;

import com.booking.dto.response.ApiResponse;
import com.booking.dto.response.SeatResponse;
import com.booking.dto.response.ShowResponse;
import com.booking.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    // Get all available shows for a movie
    // GET /api/shows/movie/1
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByMovie(
            @PathVariable Long movieId) {
        return ResponseEntity.ok(
                ApiResponse.success("Shows fetched",
                        showService.getAvailableShows(movieId)));
    }

    // Get all seats for a show — shows AVAILABLE / BOOKED / LOCKED
    // GET /api/shows/1/seats
    @GetMapping("/{showId}/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsByShow(
            @PathVariable Long showId) {
        return ResponseEntity.ok(
                ApiResponse.success("Seats fetched",
                        showService.getSeatsByShow(showId)));
    }
    @GetMapping("/{showId}")
    public ResponseEntity<ApiResponse<ShowResponse>> getShowById(
            @PathVariable Long showId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Show fetched",
                        showService.getShowById(showId)
                )
        );
    }
}