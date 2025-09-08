package com.github.connorb0531.mushroomvision.controller;
import com.github.connorb0531.mushroomvision.model.Mushroom;
import com.github.connorb0531.mushroomvision.service.MushroomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mushroom")
@CrossOrigin
public class MushroomController {
    private final MushroomService service;

    public MushroomController(MushroomService service) { this.service = service; }

    @GetMapping public List<Mushroom> list() { return service.list(); }
    @GetMapping("/{id}") public Mushroom get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mushroom create(@RequestBody Mushroom m) { return service.create(m); }

    @PatchMapping("/{id}")
    public Mushroom update(@PathVariable Long id, @RequestBody Mushroom patch) {
        return service.update(id, patch);
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    @GetMapping("/genus/{genus}")
    public List<Mushroom> byGenus(@PathVariable String genus) { return service.byGenus(genus); }
}
