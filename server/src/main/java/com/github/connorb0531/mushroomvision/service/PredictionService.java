package com.github.connorb0531.mushroomvision.service;

import com.github.connorb0531.mushroomvision.model.ImageUpload;
import com.github.connorb0531.mushroomvision.model.Prediction;
import com.github.connorb0531.mushroomvision.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository repo;

    @Transactional
    public Prediction savePrediction(ImageUpload iu, String label, Double confidence, String modelVersion, String extraJson) {
        Prediction p = new Prediction();
        p.setImage(iu);
        p.setLabel(label);
        p.setConfidence(confidence);
        p.setModelVersion(modelVersion);
        p.setExtraJson(extraJson);
        return repo.save(p);
    }
}
