package fr.ephec.altea.controller;

import fr.ephec.altea.dto.ModuleDTO;
import fr.ephec.altea.entity.Module;
import fr.ephec.altea.entity.ModuleUtilisateur;
import fr.ephec.altea.entity.Utilisateur;
import fr.ephec.altea.exception.ResourceNotFoundException;
import fr.ephec.altea.service.ModuleService;
import fr.ephec.altea.service.ModuleUtilisateurService;
import fr.ephec.altea.service.UtilisateurService;
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

    private final ModuleService moduleService;
    private final ModuleUtilisateurService moduleUtilisateurService;
    private final UtilisateurService utilisateurService;

    public ModuleController(ModuleService moduleService,
                             ModuleUtilisateurService moduleUtilisateurService,
                             UtilisateurService utilisateurService) {
        this.moduleService = moduleService;
        this.moduleUtilisateurService = moduleUtilisateurService;
        this.utilisateurService = utilisateurService;
    }

    private Utilisateur getUser(UserDetails ud) {
        return utilisateurService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    @GetMapping
    @Operation(summary = "Liste tous les modules avec activeForUser pour l'utilisateur connecté")
    public ResponseEntity<List<ModuleDTO>> getModulesForCurrentUser(@AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);

        List<String> activeModuleNames = moduleUtilisateurService
                .findByUtilisateurIdAndActifTrue(user.getId())
                .stream().map(mu -> mu.getModule().getNom()).toList();

        return ResponseEntity.ok(moduleService.findByActifTrue().stream()
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
        Module module = moduleService.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable"));

        Optional<ModuleUtilisateur> existing =
                moduleUtilisateurService.findByUtilisateurIdAndModuleId(user.getId(), moduleId);

        if (existing.isPresent()) {
            // Réactiver si désactivé
            existing.get().setActif(true);
            moduleUtilisateurService.save(existing.get());
        } else {
            // Créer la liaison
            moduleUtilisateurService.save(ModuleUtilisateur.builder()
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
        moduleUtilisateurService.findByUtilisateurIdAndModuleId(user.getId(), moduleId)
                .ifPresent(mu -> {
                    mu.setActif(false);
                    moduleUtilisateurService.save(mu);
                });
        return ResponseEntity.noContent().build();
    }
}
