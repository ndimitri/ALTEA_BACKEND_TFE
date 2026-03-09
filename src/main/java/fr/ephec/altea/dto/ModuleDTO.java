package fr.ephec.altea.dto;

import fr.ephec.altea.entity.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// ==================== Auth ====================

// ==================== Utilisateur ====================

// ==================== Patient ====================

// ==================== RendezVous ====================

// ==================== Soin ====================

// ==================== Module ====================

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModuleDTO {
    private Long id;
    private String nom;
    private String description;
    private Boolean actif;
    private Boolean activeForUser;
}
