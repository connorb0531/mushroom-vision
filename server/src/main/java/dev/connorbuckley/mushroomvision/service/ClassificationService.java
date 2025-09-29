package dev.connorbuckley.mushroomvision.service;

import dev.connorbuckley.mushroomvision.dto.ClassificationRequest;
import dev.connorbuckley.mushroomvision.dto.ClassificationResponse;
import dev.connorbuckley.mushroomvision.entity.ClassificationResult;
import dev.connorbuckley.mushroomvision.repository.ClassificationResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassificationService {
    
    private final ClassificationResultRepository repository;
    private final MLService mlService;
    
    public ClassificationResponse classifyMushroom(ClassificationRequest request) {
        // Call ML service for classification
        ClassificationResponse mlResponse = mlService.classifyImage(request.getImageData());
        
        if (!mlResponse.isSuccess()) {
            return mlResponse;
        }
        
        // Save result to database
        ClassificationResult result = ClassificationResult.builder()
            .imageName(request.getImageName())
            .imagePath(null) // Could be enhanced to store file path
            .prediction(mlResponse.getResult().getPrediction())
            .confidence(mlResponse.getResult().getConfidence())
            .edibleProbability(mlResponse.getResult().getProbabilities().getEdible())
            .poisonousProbability(mlResponse.getResult().getProbabilities().getPoisonous())
            .createdAt(LocalDateTime.now())
            .build();
        
        ClassificationResult savedResult = repository.save(result);
        
        return ClassificationResponse.builder()
            .success(true)
            .message("Classification completed and saved")
            .result(mlResponse.getResult())
            .build();
    }
    
    @Transactional(readOnly = true)
    public List<ClassificationResult> getAllClassifications() {
        return repository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<ClassificationResult> getClassificationById(Long id) {
        return repository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<ClassificationResult> getClassificationsByPrediction(String prediction) {
        return repository.findByPredictionOrderByCreatedAtDesc(prediction);
    }
    
    @Transactional(readOnly = true)
    public List<ClassificationResult> getClassificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<ClassificationResult> getClassificationsByMinConfidence(Double minConfidence) {
        return repository.findByMinConfidence(minConfidence);
    }
    
    @Transactional(readOnly = true)
    public Long getCountByPrediction(String prediction) {
        return repository.countByPrediction(prediction);
    }
    
    @Transactional(readOnly = true)
    public Double getAverageConfidenceByPrediction(String prediction) {
        return repository.getAverageConfidenceByPrediction(prediction);
    }
    
    public void deleteClassification(Long id) {
        repository.deleteById(id);
    }
}
