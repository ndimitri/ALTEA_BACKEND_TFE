package fr.ephec.altea.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patient")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column()
    @Embedded
    private Address adresse;

    // Coordonnées géographiques pour Leaflet
    private Double latitude;
    private Double longitude;

    @Column(length = 20)
    private String telephone;

    @Column(length = 255)
    private String email;

    @Column(name = "informations_medicales", columnDefinition = "TEXT")
    private String informationsMedicales;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "numero_mutuelle", length = 50)
    private String numeroMutuelle;

    @Column(name = "nom_mutuelle", length = 100)
    private String nomMutuelle;

    @Column(name = "medecin_referent", length = 200)
    private String medecinReferent;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Soin> soins = new ArrayList<>();

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
