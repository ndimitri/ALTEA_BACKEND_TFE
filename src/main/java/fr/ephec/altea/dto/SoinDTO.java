package fr.ephec.altea.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SoinDTO {
    private Long id;
    @NotBlank @Size(max = 100)
    private String type;
    private String description;
    private String notes;
    private String moduleSpecifique;
    private Long patientId;
    private String patientNom;
    private Long moduleId;
    private String moduleNom;
    private Long rendezVousId;
    private LocalDateTime createdAt;
}
