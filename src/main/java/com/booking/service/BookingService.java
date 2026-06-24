package com.booking.service;

import com.booking.dto.request.BookingRequest;
import com.booking.dto.response.BookingResponse;
import com.booking.entity.*;
import com.booking.enums.BookingStatus;
import com.booking.enums.PaymentStatus;
import com.booking.enums.SeatStatus;
import com.booking.exception.ResourceNotFoundException;
import com.booking.exception.SeatUnavailableException;
import com.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ByteArrayResource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository  bookingRepository;
    private final SeatRepository     seatRepository;
    private final ShowRepository     showRepository;
    private final UserRepository     userRepository;
    private final EmailService emailService;
    private final QRCodeService qrCodeService;
    private final PdfService pdfService;
    // ─── BOOK A SEAT ────────────────────────────────────────────────────────
    // @Transactional ensures ALL steps succeed or ALL roll back
    // This is the method that prevents double booking
    @Transactional
    public BookingResponse bookSeat(BookingRequest request) {

        // 1. Get the currently logged-in user from JWT context
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // 2. Load the show
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found: " + request.getShowId()));

        // 3. Check show-level availability fast — O(1) counter check
        if (show.getAvailableSeats() <= 0) {
            throw new SeatUnavailableException(
                    "No seats available for this show");
        }

        // 4. Load the seat — this read is protected by @Version
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat not found: " + request.getSeatId()));

        // 5. Check seat belongs to the requested show
        if (!seat.getShow().getId().equals(request.getShowId())) {
            throw new SeatUnavailableException(
                    "Seat does not belong to this show");
        }

        // 6. Check seat status — reject if already taken
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatUnavailableException(
                    "Seat " + seat.getSeatNumber() + " is not available");
        }

        // 7. Mark seat as BOOKED
        // Hibernate's @Version fires here:
        // UPDATE seats SET status='BOOKED', version=1
        // WHERE id=? AND version=0
        // If another transaction already updated version,
        // this throws ObjectOptimisticLockingFailureException
        // which GlobalExceptionHandler catches → 409 response
        seat.setStatus(SeatStatus.BOOKED);

        // 8. Decrement available seats counter on the show
        show.setAvailableSeats(show.getAvailableSeats() - 1);

        // 9. Create the booking record
        Booking booking = Booking.builder()
                .user(user)
                .seat(seat)
                .status(BookingStatus.CONFIRMED)
                .build();
        bookingRepository.save(booking);

        // 10. Simulate payment — real system would call payment gateway
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(show.getMovie().getPrice())
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId(UUID.randomUUID().toString())
                .build();
        booking.setPayment(payment);

        // 11. Save everything — @Transactional commits all at once
        bookingRepository.save(booking);

        emailService.sendBookingConfirmation(
                user.getEmail(),
                show.getMovie().getTitle(),
                show.getTheaterName(),
                seat.getSeatNumber()
        );

        String qrData =
                "Booking ID: " + booking.getId()
                + "\nMovie: " + show.getMovie().getTitle()
                + "\nTheater: " + show.getTheaterName()
                + "\nSeat: " + seat.getSeatNumber();

        String qrCodeUrl =
                qrCodeService.generateQRCode(
                        qrData,
                        "booking_" + booking.getId()
                );

        booking.setQrCodeUrl(qrCodeUrl);

        bookingRepository.save(booking);

        return toResponse(
                booking,
                show,
                payment,
                qrCodeUrl
        );
    }

    // ─── CANCEL A BOOKING ───────────────────────────────────────────────────
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found: " + bookingId));

        // Security check — user can only cancel their own booking
        if (!booking.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        // Already cancelled check
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        // 1. Free the seat back
        Seat seat = booking.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);

        // 2. Increment available seats counter
        Show show = seat.getShow();
        show.setAvailableSeats(show.getAvailableSeats() + 1);

        // 3. Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        // 4. Refund payment
        if (booking.getPayment() != null) {
            booking.getPayment().setPaymentStatus(PaymentStatus.REFUNDED);
        }

        bookingRepository.save(booking);

        return toResponse(
                booking,
                show,
                booking.getPayment(),
                null
        );
    }
    @Transactional(readOnly = true)
    public Page<BookingResponse> getMyBookings(int page, int size) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return bookingRepository
                .findByUserId(user.getId(),
                        PageRequest.of(page, size,
                                Sort.by("bookedAt").descending()))
                .map(b -> toResponse(
                        b,
                        b.getSeat().getShow(),
                        b.getPayment(),
                        b.getQrCodeUrl()));
    }

    // ─── MAP TO RESPONSE DTO ─────────────────────────────────────────────────
    private BookingResponse toResponse(
            Booking booking,
            Show show,
            Payment payment,
            String qrCodeUrl) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .movieTitle(show.getMovie().getTitle())
                .theaterName(show.getTheaterName())
                .showTime(show.getShowTime())
                .seatNumber(booking.getSeat().getSeatNumber())
                .amountPaid(payment != null
                        ? payment.getAmount()
                        : BigDecimal.ZERO)
                .bookingStatus(booking.getStatus())
                .paymentStatus(payment != null
                        ? payment.getPaymentStatus()
                        : PaymentStatus.PENDING)
                .bookedAt(booking.getBookedAt())
                .qrCodeUrl(booking.getQrCodeUrl())
                .build();
    }
    
    @Transactional(readOnly = true)
    public byte[] downloadTicket(Long bookingId) {

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"));

        return pdfService.generateTicketPdf(
                booking
        );
    }
}