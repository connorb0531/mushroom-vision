package com.github.connorb0531.mushroomvision.service;

import com.github.connorb0531.mushroomvision.model.ImageUpload;
import com.github.connorb0531.mushroomvision.repository.ImageUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final ImageUploadRepository repo;
    private final StorageService storageService; // abstraction for S3/local uploads

    @Transactional
    public ImageUpload createFromUrl(String imageUrl,
            Long uploaderId,
            String source,
            Long filesize,
            Integer width,
            Integer height,
            Double gpsLat,
            Double gpsLon,
            OffsetDateTime shotAt) {
        ImageUpload iu = new ImageUpload();
        iu.setImageUrl(imageUrl);
        iu.setThumbUrl(null);
        iu.setUploaderId(uploaderId);
        iu.setSource(Optional.ofNullable(source).orElse("USER"));
        iu.setFilesize(filesize);
        iu.setWidth(width);
        iu.setHeight(height);
        iu.setGpsLat(gpsLat);
        iu.setGpsLon(gpsLon);
        iu.setShotAt(shotAt != null ? shotAt.toInstant() : null);
        iu.setCreatedAt(OffsetDateTime.now().toInstant());
        iu.setStatus(ImageUpload.Status.PENDING);
        return repo.save(iu);
    }

    @Transactional
    public ImageUpload createFromMultipart(MultipartFile file, Long uploaderId) {
        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file");
        if (!file.getContentType().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Not an image");
        }
        if (file.getSize() > 15 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Max 15MB");
        }

        // persist to storage (local or S3)
        String url = storageService.put(file);

        return createFromUrl(
                url,
                uploaderId,
                "USER",
                file.getSize(),
                null, null,
                null, null,
                null);
    }

    @Transactional(readOnly = true)
    public ImageUpload get(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
    }

    @Transactional
    public ImageUpload markPredicted(Long id) {
        ImageUpload iu = get(id);
        iu.setStatus(ImageUpload.Status.PREDICTED);
        return repo.save(iu);
    }

    @Transactional
    public void markFailed(Long id, String reason) {
        ImageUpload iu = get(id);
        iu.setStatus(ImageUpload.Status.FAILED);
        iu.setFailureReason(reason);
        repo.save(iu);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ImageUpload> list(ImageUpload.Status status,
            org.springframework.data.domain.Pageable pageable) {
        return (status == null)
                ? repo.findAll(pageable)
                : repo.findByStatus(status, pageable);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        repo.deleteById(id);
    }
}
