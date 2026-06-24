package com.booking.controller;

import com.booking.dto.response.ApiResponse;
import com.booking.dto.response.MovieResponse;
import com.booking.entity.Movie;
import com.booking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // Public — anyone can browse movies
    // GET /api/movies?page=0&size=10&city=Chennai&genre=Action
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getAllMovies(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MovieResponse> movies =
                movieService.getAllMovies(city, genre, title, page, size);
        return ResponseEntity.ok(
                ApiResponse.success("Movies fetched successfully", movies));
    }

    // Public — get single movie
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Movie fetched",
                        movieService.getMovieById(id)));
    }

    // ADMIN only — add a movie
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponse>> addMovie(
            @RequestBody Movie movie) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movie added",
                        movieService.addMovie(movie)));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @PathVariable Long id,
            @RequestBody Movie movie) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Movie updated successfully",
                        movieService.updateMovie(id, movie)
                )
        );
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteMovie(
            @PathVariable Long id) {

        movieService.deleteMovie(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Movie deleted successfully",
                        "Deleted"
                )
        );
    }
}