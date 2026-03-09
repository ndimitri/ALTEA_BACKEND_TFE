package fr.ephec.altea.dto;

import fr.ephec.altea.entity.Address;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RendezVousDTO {
    private Long id;
    @NotNull
    private LocalDateTime dateHeureDebut;
    @NotNull
    private LocalDateTime dateHeureFin;
    private Address lieu;
    private String commentaire;
    private String statut;
    private String couleur;
    private Long patientId;
    private String patientNom;
    private String patientPrenom;
    private Address patientAdresse;
    private LocalDateTime createdAt;
}
