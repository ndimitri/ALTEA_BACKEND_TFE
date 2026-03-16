package fr.ephec.altea.dto;

import fr.ephec.altea.entity.Role;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String fullName;
    private String email;
    private String telephone;
    private Role role;
    private Boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long patientCount;
    private long rendezVousCount;
    private long activeModuleCount;
    private List<String> activeModules;
    private long accountAgeDays;
    private String refreshedToken;
}

