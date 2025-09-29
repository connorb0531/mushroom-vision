package dev.connorbuckley.mushroomvision.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassificationRequest {
    private String imageData; // Base64 encoded image
    private String imageName;
}
