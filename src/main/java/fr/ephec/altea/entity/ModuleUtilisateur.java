package fr.ephec.altea.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "module_utilisateur",
       uniqueConstraints = @UniqueConstraint(columnNames = {"utilisateur_id", "module_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModuleUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "date_activation")
    private LocalDateTime dateActivation;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @PrePersist
    void onCreate() {
        this.dateActivation = LocalDateTime.now();
    }
}
