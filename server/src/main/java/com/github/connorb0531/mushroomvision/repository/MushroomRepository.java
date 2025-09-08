package com.github.connorb0531.mushroomvision.repository;
import com.github.connorb0531.mushroomvision.model.Mushroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MushroomRepository extends JpaRepository<Mushroom, Long> {
    List<Mushroom> findByGenusIgnoreCase(String genus);
}
