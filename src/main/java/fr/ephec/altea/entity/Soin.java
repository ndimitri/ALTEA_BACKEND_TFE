package fr.ephec.altea.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "soin")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Soin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_soin", nullable = true)
    private LocalDateTime dateSoin;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Champs spécifiques modules
    @Column(name = "module_specifique", length = 50)
    private String moduleSpecifique;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rdv_id", nullable = true)
    private RendezVous rendezVous;


    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
