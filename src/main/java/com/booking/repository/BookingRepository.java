package com.booking.repository;

import com.booking.entity.Booking;
import com.booking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Get paginated bookings for a user
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    // Get bookings by user and status
    List<Booking> findByUserIdAndStatus(
            Long userId,
            BookingStatus status
    );

    // Check if seat already booked
    boolean existsBySeatIdAndStatus(
            Long seatId,
            BookingStatus status
    );

    // Check if any booking exists for a show
    boolean existsBySeatShowId(Long showId);

    // Check if confirmed booking exists for a show
    boolean existsBySeatShowIdAndStatus(
            Long showId,
            BookingStatus status
    );

    // Get all bookings of a show
    List<Booking> findBySeatShowId(Long showId);

    // Delete bookings of a show
    void deleteBySeatShowId(Long showId);

    // Revenue dashboard
    long countByStatus(BookingStatus status);

    @Query("""
           SELECT COALESCE(SUM(p.amount), 0)
           FROM Payment p
           WHERE p.paymentStatus = 'SUCCESS'
           """)
    BigDecimal getTotalRevenue();
}