package com.booking.service;

import com.booking.dto.response.MovieResponse;
import com.booking.entity.Movie;
import com.booking.exception.ResourceNotFoundException;
import com.booking.repository.MovieRepository;
import com.booking.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    public Page<MovieResponse> getAllMovies(String city,
                                            String genre,
                                            String title,
                                            int page,
                                            int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("releaseDate").descending());

        // Convert empty string to null
        // so IS NULL check works correctly in JPQL
        String cityParam  = (city  != null && !city.trim().isEmpty())
                ? city.trim()  : null;
        String genreParam = (genre != null && !genre.trim().isEmpty())
                ? genre.trim() : null;
        String titleParam = (title != null && !title.trim().isEmpty())
                ? title.trim() : null;

        return movieRepository
                .findWithFilters(cityParam, genreParam, titleParam, pageable)
                .map(this::toResponse);
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + id));
        return toResponse(movie);
    }

    @Transactional
    public MovieResponse addMovie(Movie movie) {
        Movie saved = movieRepository.save(movie);
        return toResponse(saved);
    }
    @Transactional
    public MovieResponse updateMovie(Long id, Movie movieRequest) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + id));

        movie.setTitle(movieRequest.getTitle());
        movie.setGenre(movieRequest.getGenre());
        movie.setDurationMins(movieRequest.getDurationMins());
        movie.setPrice(movieRequest.getPrice());
        movie.setLanguage(movieRequest.getLanguage());
        movie.setCity(movieRequest.getCity());
        movie.setReleaseDate(movieRequest.getReleaseDate());
        movie.setDescription(movieRequest.getDescription());
        movie.setPosterUrl(movieRequest.getPosterUrl());

        return toResponse(movieRepository.save(movie));
    }

    @Transactional
    public void deleteMovie(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + id));

        if (showRepository.existsByMovieId(id)) {
            throw new RuntimeException(
                    "Cannot delete movie. Shows already exist for this movie.");
        }

        movieRepository.delete(movie);
    }
    private MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .durationMins(movie.getDurationMins())
                .price(movie.getPrice())
                .language(movie.getLanguage())
                .city(movie.getCity())
                .releaseDate(movie.getReleaseDate())
                .description(movie.getDescription())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
  
    
}