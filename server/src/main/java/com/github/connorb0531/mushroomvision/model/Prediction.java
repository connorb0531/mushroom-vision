package com.github.connorb0531.mushroomvision.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "predictions")
@Getter
@Setter
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "image_id", nullable = false)
    ImageUpload image;

    String label;
    Double confidence;
    String modelVersion;

    @Lob
    String extraJson;

    Instant createdAt = Instant.now();
}
