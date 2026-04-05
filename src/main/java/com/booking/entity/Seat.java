package com.booking.entity;

import com.booking.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats",
       indexes = { @Index(name = "idx_seat_show", columnList = "show_id") },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_seat_show_number",
                            columnNames = { "show_id", "seat_number" })
       })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(nullable = false, length = 10)
    private String seatNumber;  // e.g. "A1", "B12"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Version  // ← THIS is the optimistic lock column. Critical for concurrency.
    private Integer version;
}