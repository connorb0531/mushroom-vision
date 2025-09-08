package com.github.connorb0531.mushroomvision.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
    public String put(MultipartFile file) {
        // TODO: implement (local disk or S3)
        // return URL accessible by worker
        return "http://localhost:8080/static/" + file.getOriginalFilename();
    }

    public String publicUrl(String key) {
        return key;
    }
}
