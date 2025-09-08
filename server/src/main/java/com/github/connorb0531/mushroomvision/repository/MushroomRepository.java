package com.github.connorb0531.mushroomvision.repository;

import com.github.connorb0531.mushroomvision.model.Mushroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MushroomRepository extends JpaRepository<Mushroom, Long> {
    Optional<Mushroom> findByCommonNameIgnoreCase(String commonName);
}
