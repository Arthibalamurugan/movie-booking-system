// response/MovieResponse.java
package com.booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String genre;
    private Integer durationMins;
    private BigDecimal price;
    private String language;
    private String city;
    private LocalDate releaseDate;
    private String description;
    private String posterUrl;
}