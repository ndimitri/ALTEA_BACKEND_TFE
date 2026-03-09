package fr.ephec.altea.controller;

import fr.ephec.altea.dto.ModuleDTO;
import fr.ephec.altea.entity.Utilisateur;
import fr.ephec.altea.exception.ResourceNotFoundException;
import fr.ephec.altea.repository.ModuleRepository;
import fr.ephec.altea.repository.ModuleUtilisateurRepository;
import fr.ephec.altea.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public ResponseEntity<List<ModuleDTO>> getModulesForCurrentUser(@AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = utilisateurRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

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
}
