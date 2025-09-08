package com.github.connorb0531.mushroomvision.controller;

import com.github.connorb0531.mushroomvision.model.ImageUpload;
import com.github.connorb0531.mushroomvision.model.Prediction;
import com.github.connorb0531.mushroomvision.service.ImageUploadService;
import com.github.connorb0531.mushroomvision.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin
public class ImageUploadController {

    private final ImageUploadService service;
    private final PredictionService predictionService;

    // Upload from multipart
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageUpload upload(@RequestPart("file") MultipartFile file,
            @RequestParam(value = "uploaderId", required = false) Long uploaderId) {
        return service.createFromMultipart(file, uploaderId);
    }

    // Create from existing URL
    @PostMapping("/url")
    public ImageUpload createFromUrl(@RequestBody CreateFromUrlRequest req) {
        return service.createFromUrl(
                req.imageUrl(), req.uploaderId(), req.source(), req.filesize(),
                req.width(), req.height(), req.shotAt());
    }

    @GetMapping
    public Page<ImageUpload> list(
            @RequestParam(value = "status", required = false) ImageUpload.Status status,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.list(status, pageable);
    }

    @GetMapping("/{id}")
    public ImageUpload get(@PathVariable Long id) {
        return service.get(id);
    }

    // Save prediction (called by worker)
    @PostMapping("/{id}/prediction")
    public Prediction savePrediction(@PathVariable Long id, @RequestBody PredictionRequest req) {
        ImageUpload iu = service.get(id);
        Prediction p = predictionService.savePrediction(iu, req.label(), req.confidence(), req.modelVersion(),
                req.extraJson());
        service.markPredicted(id);
        return p;
    }

    @GetMapping("/{id}/prediction")
    public Prediction getPrediction(@PathVariable Long id) {
        ImageUpload iu = service.get(id);
        if (iu.getPrediction() == null) {
            throw new RuntimeException("Prediction not ready");
        }
        return iu.getPrediction();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // --- DTOs ---
    public record CreateFromUrlRequest(
            String imageUrl,
            Long uploaderId,
            String source,
            Long filesize,
            Integer width,
            Integer height,
            Double gpsLat,
            Double gpsLon,
            OffsetDateTime shotAt) {
    }

    public record PredictionRequest(
            String label,
            Double confidence,
            String modelVersion,
            String extraJson) {
    }
}
