package dev.connorbuckley.mushroomvision.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificationResponse {
    private boolean success;
    private String message;
    private ClassificationResult result;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClassificationResult {
        private String prediction;
        private Double confidence;
        private Probabilities probabilities;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Probabilities {
            private Double edible;
            private Double poisonous;
        }
    }
}
