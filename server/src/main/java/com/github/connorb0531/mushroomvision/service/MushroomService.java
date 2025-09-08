package com.github.connorb0531.mushroomvision.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.connorb0531.mushroomvision.model.Mushroom;
import com.github.connorb0531.mushroomvision.repository.MushroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.util.List;

@Service
public class MushroomService {
    private final MushroomRepository repo;
    private final ObjectMapper mapper;

    public MushroomService(MushroomRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<Mushroom> list() {
        return repo.findAll();
    }

    public Mushroom get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Mushroom create(Mushroom m) {
        return repo.save(m);
    }

    public Mushroom update(Long id, Mushroom patch) {
        Mushroom m = get(id);
        if (patch.getCommonName() != null)
            m.setCommonName(patch.getCommonName());
        return repo.save(m);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // --- New: Import mushrooms from JSON file ---
    @Transactional
    public ImportResult importJsonFile(MultipartFile file, boolean upsert) throws Exception {
        try (InputStream in = file.getInputStream()) {
            List<Mushroom> mushrooms = mapper.readValue(in, new TypeReference<>() {
            });
            int created = 0, updated = 0;

            for (Mushroom m : mushrooms) {
                if (m.getCommonName() == null || m.getCommonName().isBlank())
                    continue;

                var existing = repo.findByCommonNameIgnoreCase(m.getCommonName());
                if (existing.isPresent()) {
                    if (upsert) {
                        Mushroom e = existing.get();
                        e.setCommonName(m.getCommonName()); // redundant, but safe
                        repo.save(e);
                        updated++;
                    }
                } else {
                    repo.save(m);
                    created++;
                }
            }
            return new ImportResult(mushrooms.size(), created, updated);
        }
    }

    public record ImportResult(int total, int created, int updated) {
    }
}
