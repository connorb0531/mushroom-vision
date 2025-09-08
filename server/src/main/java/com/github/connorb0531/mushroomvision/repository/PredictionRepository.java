package com.github.connorb0531.mushroomvision.repository;

import com.github.connorb0531.mushroomvision.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {}
