package fr.ephec.altea.dto;

import fr.ephec.altea.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UtilisateurDTO {
    private Long id;
    @NotBlank @Size(max = 100)
    private String nom;
    @NotBlank @Size(max = 100)
    private String prenom;
    @NotBlank @Email
    private String email;
    private String telephone;
    private Role role;
    private Boolean actif;
    private LocalDateTime createdAt;
}
