package com.github.connorb0531.mushroomvision.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "image_uploads")
@Getter @Setter
public class ImageUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long uploaderId;

    @Column(nullable = false)
    private String source;                // default "USER"

    @Column(nullable = false, length = 2048)
    private String imageUrl;

    @Column(nullable = false, length = 2048)
    private String thumbUrl;              // default to imageUrl if no real thumb yet

    @Column(nullable = false)
    private String mimeType;              // default "application/octet-stream" if missing

    @Column(nullable = false)
    private Long filesize;                // bytes

    @Column
    private Integer width;                // pixels (null only if probe fails)

    @Column
    private Integer height;               // pixels (null only if probe fails)

    @Column
    private Double gpsLat;

    @Column
    private Double gpsLon;

    @Column
    private Instant shotAt;               // EXIF DateTimeOriginal if available

    @Column(nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;                // PENDING/RUNNING/PREDICTED/FAILED

    @Column
    private String failureReason;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL)
    private Prediction prediction;

    public enum Status { PENDING, RUNNING, PREDICTED, FAILED }

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = Status.PENDING;
        if (source == null) source = "USER";
        if (mimeType == null) mimeType = "application/octet-stream";
        // Avoid null thumbUrl if nothing else set it
        if (thumbUrl == null) thumbUrl = imageUrl;
    }
}
