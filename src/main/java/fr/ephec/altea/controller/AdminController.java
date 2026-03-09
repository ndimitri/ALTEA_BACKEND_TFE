package fr.ephec.altea.controller;

import fr.ephec.altea.dto.*;
import fr.ephec.altea.entity.*;
import fr.ephec.altea.entity.Module;
import fr.ephec.altea.exception.*;
import fr.ephec.altea.repository.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ==================== ADMIN ====================

@RestController
@RequestMapping("/admin")
@Tag(name = "Administration")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UtilisateurRepository utilisateurRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleUtilisateurRepository moduleUtilisateurRepository;

    AdminController(UtilisateurRepository utilisateurRepository,
                    ModuleRepository moduleRepository,
                    ModuleUtilisateurRepository moduleUtilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.moduleRepository = moduleRepository;
        this.moduleUtilisateurRepository = moduleUtilisateurRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UtilisateurDTO>> getAllUsers() {
        return ResponseEntity.ok(utilisateurRepository.findAll()
                .stream().map(this::toDTO).toList());
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<UtilisateurDTO> toggleUser(@PathVariable Long id) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setActif(!user.getActif());
        return ResponseEntity.ok(toDTO(utilisateurRepository.save(user)));
    }

    @PostMapping("/users/{userId}/modules/{moduleId}")
    public ResponseEntity<Void> activateModule(@PathVariable Long userId, @PathVariable Long moduleId) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable"));

        moduleUtilisateurRepository.findByUtilisateurIdAndModuleId(userId, moduleId)
                .ifPresentOrElse(
                        mu -> { mu.setActif(true); moduleUtilisateurRepository.save(mu); },
                        () -> moduleUtilisateurRepository.save(
                                ModuleUtilisateur.builder().utilisateur(user).module(module).actif(true).build())
                );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}/modules/{moduleId}")
    public ResponseEntity<Void> deactivateModule(@PathVariable Long userId, @PathVariable Long moduleId) {
        moduleUtilisateurRepository.findByUtilisateurIdAndModuleId(userId, moduleId)
                .ifPresent(mu -> { mu.setActif(false); moduleUtilisateurRepository.save(mu); });
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

