package com.github.connorb0531.mushroomvision.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="feedback")
@Getter
@Setter
public class Feedback {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne @JoinColumn(name="image_id") ImageUpload image;
    Long userId;
    String userLabel; Boolean isCorrect;
    @Column(length=2000) String notes;
    Instant createdAt = Instant.now();
}
