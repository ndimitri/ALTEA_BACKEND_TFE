package fr.ephec.altea.controller;

import fr.ephec.altea.dto.*;
import fr.ephec.altea.entity.*;
import fr.ephec.altea.entity.Module;
import fr.ephec.altea.exception.*;
import fr.ephec.altea.service.UtilisateurService;
import fr.ephec.altea.service.ModuleService;
import fr.ephec.altea.service.ModuleUtilisateurService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ==================== ADMIN ====================

@RestController
@RequestMapping("/admin")
@Tag(name = "Administration")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UtilisateurService utilisateurService;
    private final ModuleService moduleService;
    private final ModuleUtilisateurService moduleUtilisateurService;

    AdminController(UtilisateurService utilisateurService,
                    ModuleService moduleService,
                    ModuleUtilisateurService moduleUtilisateurService) {
        this.utilisateurService = utilisateurService;
        this.moduleService = moduleService;
        this.moduleUtilisateurService = moduleUtilisateurService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UtilisateurDTO>> getAllUsers() {
        return ResponseEntity.ok(utilisateurService.findAll()
                .stream().map(this::toDTO).toList());
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<UtilisateurDTO> toggleUser(@PathVariable Long id) {
        Utilisateur user = utilisateurService.toggleUserStatus(id);
        return ResponseEntity.ok(toDTO(user));
    }

    @PostMapping("/users/{userId}/modules/{moduleId}")
    public ResponseEntity<Void> activateModule(@PathVariable Long userId, @PathVariable Long moduleId) {
        Utilisateur user = utilisateurService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        Module module = moduleService.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable"));

        moduleUtilisateurService.findByUtilisateurIdAndModuleId(userId, moduleId)
                .ifPresentOrElse(
                        mu -> { mu.setActif(true); moduleUtilisateurService.save(mu); },
                        () -> moduleUtilisateurService.save(
                                ModuleUtilisateur.builder().utilisateur(user).module(module).actif(true).build())
                );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}/modules/{moduleId}")
    public ResponseEntity<Void> deactivateModule(@PathVariable Long userId, @PathVariable Long moduleId) {
        moduleUtilisateurService.findByUtilisateurIdAndModuleId(userId, moduleId)
                .ifPresent(mu -> { mu.setActif(false); moduleUtilisateurService.save(mu); });
        return ResponseEntity.ok().build();
    }

    private UtilisateurDTO toDTO(Utilisateur u) {
        return UtilisateurDTO.builder()
                .id(u.getId()).nom(u.getNom()).prenom(u.getPrenom())
                .email(u.getEmail()).telephone(u.getTelephone())
                .role(u.getRole()).actif(u.getActif()).createdAt(u.getCreatedAt())
                .build();
    }
}

// ==================== MODULES (pour user courant) ====================

