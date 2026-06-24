package com.booking.service;

import com.booking.dto.response.AnalyticsResponse;
import com.booking.dto.response.RevenueResponse;
import com.booking.enums.BookingStatus;
import com.booking.repository.BookingRepository;
import com.booking.repository.MovieRepository;
import com.booking.repository.ShowRepository;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.booking.dto.response.UserResponse;
import com.booking.dto.request.UpdateRoleRequest;
import com.booking.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    
    public UserResponse updateUserRole(
            Long userId,
            UpdateRoleRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setRole(request.getRole());

        userRepository.save(user);

        return toUserResponse(user);
    }
    // Revenue Dashboard
    public RevenueResponse getRevenue() {

        return RevenueResponse.builder()
                .totalBookings(bookingRepository.count())
                .confirmedBookings(
                        bookingRepository.countByStatus(
                                BookingStatus.CONFIRMED))
                .cancelledBookings(
                        bookingRepository.countByStatus(
                                BookingStatus.CANCELLED))
                .totalRevenue(
                        bookingRepository.getTotalRevenue())
                .build();
    }

    // Analytics Dashboard
    public AnalyticsResponse getAnalytics() {

        long totalUsers = userRepository.count();
        long totalMovies = movieRepository.count();
        long totalShows = showRepository.count();
        long totalBookings = bookingRepository.count();

        double occupancyRate = 0.0;

        if (totalShows > 0) {
            occupancyRate =
                    ((double) totalBookings / (totalShows * 50)) * 100;
        }

        return AnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .totalMovies(totalMovies)
                .totalShows(totalShows)
                .totalBookings(totalBookings)
                .occupancyRate(
                        Math.round(occupancyRate * 100.0) / 100.0)
                .build();
    }
}