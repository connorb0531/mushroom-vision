package dev.connorbuckley.mushroomvision.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "classification_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificationResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "image_name", nullable = false)
    private String imageName;
    
    @Column(name = "image_path")
    private String imagePath;
    
    @Column(name = "prediction", nullable = false)
    private String prediction; // "edible" or "poisonous"
    
    @Column(name = "confidence", nullable = false)
    private Double confidence;
    
    @Column(name = "edible_probability", nullable = false)
    private Double edibleProbability;
    
    @Column(name = "poisonous_probability", nullable = false)
    private Double poisonousProbability;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
