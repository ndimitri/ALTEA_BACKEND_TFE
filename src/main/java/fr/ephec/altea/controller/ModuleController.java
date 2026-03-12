package fr.ephec.altea.controller;

import fr.ephec.altea.dto.ModuleDTO;
import fr.ephec.altea.entity.Module;
import fr.ephec.altea.entity.ModuleUtilisateur;
import fr.ephec.altea.entity.Utilisateur;
import fr.ephec.altea.exception.ResourceNotFoundException;
import fr.ephec.altea.repository.ModuleRepository;
import fr.ephec.altea.repository.ModuleUtilisateurRepository;
import fr.ephec.altea.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modules")
@Tag(name = "Modules métiers")
@SecurityRequirement(name = "bearerAuth")
public class ModuleController {

    private final ModuleRepository moduleRepository;
    private final ModuleUtilisateurRepository moduleUtilisateurRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ModuleController(ModuleRepository moduleRepository,
                             ModuleUtilisateurRepository moduleUtilisateurRepository,
                             UtilisateurRepository utilisateurRepository) {
        this.moduleRepository = moduleRepository;
        this.moduleUtilisateurRepository = moduleUtilisateurRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    private Utilisateur getUser(UserDetails ud) {
        return utilisateurRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    @GetMapping
    @Operation(summary = "Liste tous les modules avec activeForUser pour l'utilisateur connecté")
    public ResponseEntity<List<ModuleDTO>> getModulesForCurrentUser(@AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);

        List<String> activeModuleNames = moduleUtilisateurRepository
                .findByUtilisateurIdAndActifTrue(user.getId())
                .stream().map(mu -> mu.getModule().getNom()).toList();

        return ResponseEntity.ok(moduleRepository.findByActifTrue().stream()
                .map(m -> ModuleDTO.builder()
                        .id(m.getId()).nom(m.getNom()).description(m.getDescription()).actif(m.getActif())
                        .activeForUser(activeModuleNames.contains(m.getNom()))
                        .build())
                .toList());
    }

    @PostMapping("/{moduleId}/activer")
    @Operation(summary = "Activer un module pour l'utilisateur connecté")
    public ResponseEntity<ModuleDTO> activerModule(@PathVariable Long moduleId,
                                                    @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable"));

        Optional<ModuleUtilisateur> existing =
                moduleUtilisateurRepository.findByUtilisateurIdAndModuleId(user.getId(), moduleId);

        if (existing.isPresent()) {
            // Réactiver si désactivé
            existing.get().setActif(true);
            moduleUtilisateurRepository.save(existing.get());
        } else {
            // Créer la liaison
            moduleUtilisateurRepository.save(ModuleUtilisateur.builder()
                    .utilisateur(user).module(module).actif(true).build());
        }

        return ResponseEntity.ok(ModuleDTO.builder()
                .id(module.getId()).nom(module.getNom())
                .description(module.getDescription()).actif(module.getActif())
                .activeForUser(true)
                .build());
    }

    @DeleteMapping("/{moduleId}/desactiver")
    @Operation(summary = "Désactiver un module pour l'utilisateur connecté")
    public ResponseEntity<Void> desactiverModule(@PathVariable Long moduleId,
                                                  @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        moduleUtilisateurRepository.findByUtilisateurIdAndModuleId(user.getId(), moduleId)
                .ifPresent(mu -> {
                    mu.setActif(false);
                    moduleUtilisateurRepository.save(mu);
                });
        return ResponseEntity.noContent().build();
    }
}
