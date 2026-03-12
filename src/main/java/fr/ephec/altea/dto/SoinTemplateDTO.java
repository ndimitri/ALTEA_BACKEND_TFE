package fr.ephec.altea.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SoinTemplateDTO {
  private Long id;
  @NotBlank
  private String nom;
  @NotBlank
  private String type;
  private String description;
  private String notes;
  private String moduleSpecifique;
  private Long moduleId;
  private String moduleNom;
}
