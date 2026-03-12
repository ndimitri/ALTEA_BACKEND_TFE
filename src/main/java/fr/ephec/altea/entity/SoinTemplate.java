package fr.ephec.altea.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "soin_template")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SoinTemplate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nom;

  @Column(nullable = false, length = 100)
  private String type;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(name = "module_specifique", length = 50)
  private String moduleSpecifique;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "utilisateur_id", nullable = false)
  private Utilisateur utilisateur;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "module_id")
  private Module module;
}

