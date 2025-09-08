package com.github.connorb0531.mushroomvision.repository;

import com.github.connorb0531.mushroomvision.model.ImageUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageUploadRepository extends JpaRepository<ImageUpload, Long> {
    Page<ImageUpload> findByStatus(ImageUpload.Status status, Pageable pageable);
}
