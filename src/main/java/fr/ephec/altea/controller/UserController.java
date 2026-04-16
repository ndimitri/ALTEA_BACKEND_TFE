package fr.ephec.altea.controller;

import fr.ephec.altea.dto.UpdateUserProfileRequest;
import fr.ephec.altea.dto.UserProfileDTO;
import fr.ephec.altea.entity.Utilisateur;
import fr.ephec.altea.exception.ConflictException;
import fr.ephec.altea.exception.ResourceNotFoundException;
import fr.ephec.altea.service.UtilisateurService;
import fr.ephec.altea.service.PatientService;
import fr.ephec.altea.service.RendezVousService;
import fr.ephec.altea.service.ModuleUtilisateurService;
import fr.ephec.altea.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Profil utilisateur")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UtilisateurService utilisateurService;
    private final PatientService patientService;
    private final RendezVousService rendezVousService;
    private final ModuleUtilisateurService moduleUtilisateurService;
    private final JwtUtils jwtUtils;

    public UserController(UtilisateurService utilisateurService,
                          PatientService patientService,
                          RendezVousService rendezVousService,
                          ModuleUtilisateurService moduleUtilisateurService,
                          JwtUtils jwtUtils) {
        this.utilisateurService = utilisateurService;
        this.patientService = patientService;
        this.rendezVousService = rendezVousService;
        this.moduleUtilisateurService = moduleUtilisateurService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/me")
    @Operation(summary = "Récupère le profil de l'utilisateur connecté")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(toProfileDTO(getUser(ud), null));
    }

    @PutMapping("/me")
    @Operation(summary = "Met à jour le profil de l'utilisateur connecté")
    public ResponseEntity<UserProfileDTO> updateCurrentUserProfile(@AuthenticationPrincipal UserDetails ud,
                                                                   @Valid @RequestBody UpdateUserProfileRequest request) {
        Utilisateur user = getUser(ud);

        String normalizedEmail = request.getEmail().trim();
        if (!user.getEmail().equalsIgnoreCase(normalizedEmail)
                && utilisateurService.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Cette adresse email est déjà utilisée");
        }

        user.setNom(request.getNom().trim());
        user.setPrenom(request.getPrenom().trim());
        user.setEmail(normalizedEmail);

        if (request.getTelephone() != null) {
            String normalizedPhone = request.getTelephone().trim();
            user.setTelephone(normalizedPhone.isEmpty() ? null : normalizedPhone);
        }

        Utilisateur savedUser = utilisateurService.save(user);
        String refreshedToken = !savedUser.getEmail().equals(ud.getUsername())
                ? jwtUtils.generateToken(savedUser.getEmail())
                : null;

        return ResponseEntity.ok(toProfileDTO(savedUser, refreshedToken));
    }

    private Utilisateur getUser(UserDetails ud) {
        return utilisateurService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    private UserProfileDTO toProfileDTO(Utilisateur user, String refreshedToken) {
        List<String> activeModules = moduleUtilisateurService.findByUtilisateurIdAndActifTrue(user.getId())
                .stream()
                .map(mu -> mu.getModule().getNom())
                .sorted()
                .toList();

        long accountAgeDays = user.getCreatedAt() == null
                ? 0
                : Math.max(0, ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now()));

        return UserProfileDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .fullName(buildFullName(user))
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .actif(user.getActif())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .patientCount(patientService.countByUtilisateurId(user.getId()))
                .rendezVousCount(rendezVousService.countByUtilisateurId(user.getId()))
                .activeModuleCount(activeModules.size())
                .activeModules(activeModules)
                .accountAgeDays(accountAgeDays)
                .refreshedToken(refreshedToken)
                .build();
    }

    private String buildFullName(Utilisateur user) {
        return (user.getPrenom() + " " + user.getNom()).trim();
    }
}

