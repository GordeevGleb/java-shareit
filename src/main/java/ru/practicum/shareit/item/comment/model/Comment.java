package ru.practicum.shareit.item.comment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENTS", schema = "PUBLIC")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "ID")
    private Long id;

@Column(name = "TEXT")
    private String text;

@ManyToOne
@JoinColumn(name = "ITEM_ID")
    private Item item;

@ManyToOne
@JoinColumn(name = "AUTHOR_ID")
    private User author;

@Column(name = "CREATE_DATE")
    private LocalDateTime created;
}
