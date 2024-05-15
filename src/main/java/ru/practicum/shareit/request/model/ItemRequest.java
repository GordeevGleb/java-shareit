package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Entity
@Table(name = "REQUESTS", schema = "PUBLIC")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @NotNull
    private User requester;

    @NotNull
    @Column(name = "CREATE_DATE")
    private LocalDateTime created;
}
