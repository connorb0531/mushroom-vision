package com.github.connorb0531.mushroomvision.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mushrooms")
@Getter
@Setter
public class Mushroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String genus;
    private String species;

    public Mushroom() {}
    public Mushroom(String genus, String species) {
        this.genus = genus; this.species = species;
    }

}
