package com.booking.service;

import com.booking.dto.response.ShowResponse;
import com.booking.enums.BookingStatus;
import com.booking.repository.BookingRepository;
import com.booking.dto.request.UpdateShowRequest;
import com.booking.dto.response.SeatResponse;
import com.booking.entity.Show;
import com.booking.entity.Seat;
import com.booking.exception.ResourceNotFoundException;
import com.booking.repository.ShowRepository;
import com.booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.booking.dto.request.CreateShowRequest;
import com.booking.entity.Movie;
import com.booking.enums.SeatStatus;
import com.booking.repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowService {

	private final ShowRepository showRepository;
	private final SeatRepository seatRepository;
	private final MovieRepository movieRepository;
	private final BookingRepository bookingRepository;

    public List<ShowResponse> getAvailableShows(Long movieId) {
        return showRepository
                .findAvailableShows(movieId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SeatResponse> getSeatsByShow(Long showId) {
        showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Show not found with id: " + showId));

        return seatRepository.findByShowId(showId)
                .stream()
                .map(this::toSeatResponse)
                .collect(Collectors.toList());
    }
   
    public List<ShowResponse> getAllShows() {

        return showRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    private ShowResponse toResponse(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())
                .theaterName(show.getTheaterName())
                .showTime(show.getShowTime())
                .totalSeats(show.getTotalSeats())
                .availableSeats(show.getAvailableSeats())
                .price(show.getMovie().getPrice())
                .build();
    }

    private SeatResponse toSeatResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .status(seat.getStatus())
                .build();
    }
    @Transactional
    public ShowResponse createShow(CreateShowRequest request) {

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movie not found"));

        Show show = Show.builder()
                .movie(movie)
                .theaterName(request.getTheaterName())
                .showTime(request.getShowTime())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .build();

        show = showRepository.save(show);

        for (int i = 1; i <= request.getTotalSeats(); i++) {

            char row = (char) ('A' + ((i - 1) / 10));
            int seatNo = ((i - 1) % 10) + 1;

            Seat seat = Seat.builder()
                    .show(show)
                    .seatNumber(row + String.valueOf(seatNo))
                    .status(SeatStatus.AVAILABLE)
                    .build();

            seatRepository.save(seat);
        }

        return toResponse(show);
    }
 @Transactional
  public ShowResponse updateShow(Long showId,
                                   UpdateShowRequest request) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found"));

        show.setTheaterName(request.getTheaterName());
        show.setShowTime(request.getShowTime());

        show = showRepository.save(show);

        return toResponse(show);
    }
 @Transactional
 public void deleteShow(Long showId) {

     Show show = showRepository.findById(showId)
             .orElseThrow(() ->
                     new ResourceNotFoundException(
                             "Show not found"));

     // Any booking history exists?
     if (bookingRepository.existsBySeatShowId(showId)) {

         throw new RuntimeException(
                 "Cannot delete show. Booking history exists.");
     }

     // Delete all seats
     seatRepository.deleteAll(
             seatRepository.findByShowId(showId));

     // Delete show
     showRepository.delete(show);
 }
 public ShowResponse getShowById(Long showId) {

	    Show show = showRepository.findById(showId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Show not found"));

	    return ShowResponse.builder()
	            .id(show.getId())
	            .movieId(show.getMovie().getId())
	            .movieTitle(show.getMovie().getTitle())
	            .theaterName(show.getTheaterName())
	            .showTime(show.getShowTime())
	            .totalSeats(show.getTotalSeats())
	            .availableSeats(show.getAvailableSeats())
	            .build();
	}
}