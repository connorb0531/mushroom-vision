package dev.connorbuckley.mushroomvision.repository;

import dev.connorbuckley.mushroomvision.entity.ClassificationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassificationResultRepository extends JpaRepository<ClassificationResult, Long> {
    
    List<ClassificationResult> findByPredictionOrderByCreatedAtDesc(String prediction);
    
    List<ClassificationResult> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT c FROM ClassificationResult c WHERE c.confidence >= :minConfidence ORDER BY c.createdAt DESC")
    List<ClassificationResult> findByMinConfidence(@Param("minConfidence") Double minConfidence);
    
    @Query("SELECT COUNT(c) FROM ClassificationResult c WHERE c.prediction = :prediction")
    Long countByPrediction(@Param("prediction") String prediction);
    
    @Query("SELECT AVG(c.confidence) FROM ClassificationResult c WHERE c.prediction = :prediction")
    Double getAverageConfidenceByPrediction(@Param("prediction") String prediction);
}
