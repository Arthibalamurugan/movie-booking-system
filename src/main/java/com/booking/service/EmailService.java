package com.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingConfirmation(
            String toEmail,
            String movieTitle,
            String theaterName,
            String seatNumber
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "🎬 Booking Confirmation"
        );

        message.setText(
                "Your booking has been confirmed.\n\n" +
                "Movie: " + movieTitle + "\n" +
                "Theater: " + theaterName + "\n" +
                "Seat: " + seatNumber + "\n\n" +
                "Thank you for booking with BookMyShow Clone."
        );

        mailSender.send(message);
    }
    public void sendPasswordResetEmail(
            String toEmail,
            String resetLink
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "🔑 Password Reset Request"
        );

        message.setText(
                "You requested a password reset.\n\n" +
                "Click the link below:\n\n" +
                resetLink +
                "\n\nThis link expires in 15 minutes."
        );

        mailSender.send(message);
    }
}