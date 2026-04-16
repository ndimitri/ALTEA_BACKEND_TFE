package fr.ephec.altea.controller;

import fr.ephec.altea.dto.SoinTemplateDTO;
import fr.ephec.altea.entity.*;
import fr.ephec.altea.exception.ResourceNotFoundException;
import fr.ephec.altea.service.SoinTemplateService;
import fr.ephec.altea.service.UtilisateurService;
import fr.ephec.altea.service.ModuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/soin-templates")
@Tag(name = "Templates de soins")
@SecurityRequirement(name = "bearerAuth")
public class SoinTemplateController {

  private final SoinTemplateService templateService;
  private final UtilisateurService utilisateurService;
  private final ModuleService moduleService;

  public SoinTemplateController(SoinTemplateService templateService,
      UtilisateurService utilisateurService,
      ModuleService moduleService) {
    this.templateService = templateService;
    this.utilisateurService = utilisateurService;
    this.moduleService = moduleService;
  }

  private Utilisateur getUser(UserDetails ud) {
    return utilisateurService.findByEmail(ud.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
  }

  private SoinTemplateDTO toDTO(SoinTemplate t) {
    return SoinTemplateDTO.builder()
        .id(t.getId()).nom(t.getNom()).type(t.getType())
        .description(t.getDescription()).notes(t.getNotes())
        .moduleSpecifique(t.getModuleSpecifique())
        .moduleId(t.getModule() != null ? t.getModule().getId() : null)
        .moduleNom(t.getModule() != null ? t.getModule().getNom() : null)
        .build();
  }

  @GetMapping
  @Operation(summary = "Templates de l'utilisateur connecté (optionnel: filtre par moduleId)")
  public ResponseEntity<List<SoinTemplateDTO>> getAll(
      @AuthenticationPrincipal UserDetails ud,
      @RequestParam(required = false) Long moduleId) {
    Utilisateur user = getUser(ud);
    List<SoinTemplate> templates;
    if (moduleId != null) {
      templates = templateService.findByUtilisateurIdAndModuleIdOrderByNomAsc(user.getId(), moduleId);
    } else {
      templates = templateService.findByUtilisateurIdOrderByNomAsc(user.getId());
    }
    return ResponseEntity.ok(templates.stream().map(this::toDTO).toList());
  }

  @GetMapping("/module/{moduleId}")
  @Operation(summary = "Templates globaux d'un module (partagés par tous les users)")
  public ResponseEntity<List<SoinTemplateDTO>> getByModule(
      @PathVariable Long moduleId,
      @AuthenticationPrincipal UserDetails ud) {
    // Vérifie juste que l'utilisateur est connecté
    getUser(ud);
    // Retourne tous les templates du module sans filtre utilisateur
    List<SoinTemplate> globaux = templateService.findByModuleIdOrderByNomAsc(moduleId);
    return ResponseEntity.ok(globaux.stream().map(this::toDTO).toList());
  }

  @GetMapping("/module/{moduleId}/all")
  @Operation(summary = "Templates globaux du module + templates personnels de l'utilisateur pour ce module")
  public ResponseEntity<List<SoinTemplateDTO>> getByModuleWithPersonnels(
      @PathVariable Long moduleId,
      @AuthenticationPrincipal UserDetails ud) {
    Utilisateur user = getUser(ud);
    // Templates globaux du module (créés par l'admin)
    List<SoinTemplate> globaux = templateService.findByModuleIdOrderByNomAsc(moduleId);
    // Templates personnels de l'utilisateur pour ce module
    List<SoinTemplate> personnels = templateService.findByUtilisateurIdAndModuleIdOrderByNomAsc(user.getId(), moduleId);

    // Fusion sans doublons (un user peut avoir ses propres templates + les globaux)
    List<SoinTemplate> tous = new ArrayList<>(globaux);
    personnels.stream()
        .filter(p -> globaux.stream().noneMatch(g -> g.getId().equals(p.getId())))
        .forEach(tous::add);

    return ResponseEntity.ok(tous.stream().map(this::toDTO).toList());
  }

  @PostMapping
  public ResponseEntity<SoinTemplateDTO> create(@Valid @RequestBody SoinTemplateDTO dto,
      @AuthenticationPrincipal UserDetails ud) {
    Utilisateur user = getUser(ud);
    SoinTemplate t = SoinTemplate.builder()
        .nom(dto.getNom()).type(dto.getType())
        .description(dto.getDescription()).notes(dto.getNotes())
        .moduleSpecifique(dto.getModuleSpecifique())
        .utilisateur(user)
        .build();
    if (dto.getModuleId() != null)
      moduleService.findById(dto.getModuleId()).ifPresent(t::setModule);
    return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(templateService.save(t)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<SoinTemplateDTO> update(@PathVariable Long id,
      @Valid @RequestBody SoinTemplateDTO dto,
      @AuthenticationPrincipal UserDetails ud) {
    Utilisateur user = getUser(ud);
    SoinTemplate t = templateService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Template introuvable"));
    if (!t.getUtilisateur().getId().equals(user.getId()))
      throw new AccessDeniedException("Accès refusé");
    t.setNom(dto.getNom());
    t.setType(dto.getType());
    t.setDescription(dto.getDescription());
    t.setNotes(dto.getNotes());
    t.setModuleSpecifique(dto.getModuleSpecifique());
    if (dto.getModuleId() != null)
      moduleService.findById(dto.getModuleId()).ifPresent(t::setModule);
    return ResponseEntity.ok(toDTO(templateService.save(t)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id,
      @AuthenticationPrincipal UserDetails ud) {
    Utilisateur user = getUser(ud);
    SoinTemplate t = templateService.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Template introuvable"));
    if (!t.getUtilisateur().getId().equals(user.getId()))
      throw new AccessDeniedException("Accès refusé");
    templateService.delete(t);
    return ResponseEntity.noContent().build();
  }
}
