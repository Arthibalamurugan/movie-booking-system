package com.booking.repository;

import com.booking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {
	boolean existsByMovieId(Long movieId);
	
    List<Show> findByMovieId(Long movieId);

    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId " +
           "AND s.availableSeats > 0")
    List<Show> findAvailableShows(@Param("movieId") Long movieId);
}