package ru.practicum.shareit.booking.model;


import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKINGS", schema = "PUBLIC")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "START_TIME")
    private LocalDateTime start;

    @Column(name = "END_TIME")
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    @ToString.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOKER_ID")
    @ToString.Exclude
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private BookingStatus bookingStatus;
}
