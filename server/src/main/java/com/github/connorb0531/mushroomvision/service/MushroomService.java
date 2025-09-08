package com.github.connorb0531.mushroomvision.service;
import com.github.connorb0531.mushroomvision.model.Mushroom;
import com.github.connorb0531.mushroomvision.repository.MushroomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MushroomService {
    private final MushroomRepository repo;

    public MushroomService(MushroomRepository repo) { this.repo = repo; }

    public List<Mushroom> list() { return repo.findAll(); }
    public Mushroom get(Long id) { return repo.findById(id).orElseThrow(); }
    public Mushroom create(Mushroom m) { return repo.save(m); }

    public Mushroom update(Long id, Mushroom patch) {
        Mushroom m = get(id);
        if (patch.getGenus() != null) m.setGenus(patch.getGenus());
        if (patch.getSpecies() != null) m.setSpecies(patch.getSpecies());
        return repo.save(m);
    }

    public void delete(Long id) { repo.deleteById(id); }

    public List<Mushroom> byGenus(String genus) { return repo.findByGenusIgnoreCase(genus); }
}

