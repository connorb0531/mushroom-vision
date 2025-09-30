package dev.connorbuckley.mushroomvision.controller;

import dev.connorbuckley.mushroomvision.dto.ClassificationRequest;
import dev.connorbuckley.mushroomvision.dto.ClassificationResponse;
import dev.connorbuckley.mushroomvision.entity.ClassificationResult;
import dev.connorbuckley.mushroomvision.service.ClassificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classifications")
@RequiredArgsConstructor
public class ClassificationController {
    
    private final ClassificationService classificationService;
    
    @PostMapping("/classify")
    public ResponseEntity<ClassificationResponse> classifyMushroom(@RequestBody ClassificationRequest request) {
        ClassificationResponse response = classificationService.classifyMushroom(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/classify-file")
    public ResponseEntity<ClassificationResponse> classifyMushroomFile(
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                ClassificationResponse errorResponse = ClassificationResponse.builder()
                    .success(false)
                    .message("File is empty")
                    .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Check file size (10MB limit)
            if (file.getSize() > 10 * 1024 * 1024) {
                ClassificationResponse errorResponse = ClassificationResponse.builder()
                    .success(false)
                    .message("File too large. Maximum size is 10MB")
                    .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                ClassificationResponse errorResponse = ClassificationResponse.builder()
                    .success(false)
                    .message("Invalid file type. Please upload an image file")
                    .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            
            // Convert file to base64
            String imageData = Base64.getEncoder().encodeToString(file.getBytes());
            
            ClassificationRequest request = ClassificationRequest.builder()
                .imageData(imageData)
                .imageName(file.getOriginalFilename())
                .build();
            
            ClassificationResponse response = classificationService.classifyMushroom(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (IOException e) {
            ClassificationResponse errorResponse = ClassificationResponse.builder()
                .success(false)
                .message("Error processing file: " + e.getMessage())
                .build();
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ClassificationResponse errorResponse = ClassificationResponse.builder()
                .success(false)
                .message("Unexpected error: " + e.getMessage())
                .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ClassificationResult>> getAllClassifications() {
        List<ClassificationResult> results = classificationService.getAllClassifications();
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClassificationResult> getClassificationById(@PathVariable Long id) {
        Optional<ClassificationResult> result = classificationService.getClassificationById(id);
        
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/prediction/{prediction}")
    public ResponseEntity<List<ClassificationResult>> getClassificationsByPrediction(
            @PathVariable String prediction) {
        List<ClassificationResult> results = classificationService.getClassificationsByPrediction(prediction);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<ClassificationResult>> getClassificationsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<ClassificationResult> results = classificationService.getClassificationsByDateRange(start, end);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/confidence/{minConfidence}")
    public ResponseEntity<List<ClassificationResult>> getClassificationsByMinConfidence(
            @PathVariable Double minConfidence) {
        List<ClassificationResult> results = classificationService.getClassificationsByMinConfidence(minConfidence);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/stats/count/{prediction}")
    public ResponseEntity<Long> getCountByPrediction(@PathVariable String prediction) {
        Long count = classificationService.getCountByPrediction(prediction);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/confidence/{prediction}")
    public ResponseEntity<Double> getAverageConfidenceByPrediction(@PathVariable String prediction) {
        Double avgConfidence = classificationService.getAverageConfidenceByPrediction(prediction);
        return ResponseEntity.ok(avgConfidence);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassification(@PathVariable Long id) {
        classificationService.deleteClassification(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/test-cors")
    public ResponseEntity<String> testCors() {
        return ResponseEntity.ok("CORS is working! Server is accessible from frontend.");
    }
}
