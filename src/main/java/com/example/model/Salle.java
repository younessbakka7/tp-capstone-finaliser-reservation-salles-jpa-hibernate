package com.example.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "salles")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(nullable = false)
    private int capacite;

    @Column(length = 255)
    private String description;

    @Version
    private int version;

    protected Salle() { }

    public Salle(String nom, int capacite, String description) {
        this.nom = nom;
        this.capacite = capacite;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public int getCapacite() { return capacite; }
    public String getDescription() { return description; }
    public int getVersion() { return version; }
    public void setNom(String nom) { this.nom = nom; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public void setDescription(String description) { this.description = description; }
}
