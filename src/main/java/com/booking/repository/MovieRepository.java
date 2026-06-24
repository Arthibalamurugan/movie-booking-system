package com.booking.repository;

import com.booking.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Page<Movie> findByCity(String city, Pageable pageable);

    Page<Movie> findByGenre(String genre, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE " +
           "(:city  IS NULL OR m.city  = :city)  AND " +
           "(:genre IS NULL OR m.genre = :genre) AND " +
           "(:title IS NULL OR LOWER(m.title) " +
           "LIKE LOWER(CONCAT('%', :title, '%')))")
    Page<Movie> findWithFilters(
            @Param("city")  String city,
            @Param("genre") String genre,
            @Param("title") String title,
            Pageable pageable);
}