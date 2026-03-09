package fr.ephec.altea.controller;

import fr.ephec.altea.dto.SoinDTO;
import fr.ephec.altea.entity.*;
import fr.ephec.altea.exception.*;
import fr.ephec.altea.repository.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/soins")
@Tag(name = "Soins")
@SecurityRequirement(name = "bearerAuth")
public class SoinController {

    private final SoinRepository soinRepository;
    private final PatientRepository patientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ModuleRepository moduleRepository;

    public SoinController(SoinRepository soinRepository, PatientRepository patientRepository,
                           UtilisateurRepository utilisateurRepository, ModuleRepository moduleRepository) {
        this.soinRepository = soinRepository;
        this.patientRepository = patientRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.moduleRepository = moduleRepository;
    }

    private Utilisateur getUser(UserDetails ud) {
        return utilisateurRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    private SoinDTO toDTO(Soin s) {
        return SoinDTO.builder()
                .id(s.getId()).type(s.getType()).description(s.getDescription())
                .dateSoin(s.getDateSoin()).notes(s.getNotes())
                .moduleSpecifique(s.getModuleSpecifique())
                .patientId(s.getPatient().getId())
                .patientNom(s.getPatient().getNom() + " " + s.getPatient().getPrenom())
                .moduleId(s.getModule() != null ? s.getModule().getId() : null)
                .moduleNom(s.getModule() != null ? s.getModule().getNom() : null)
                .createdAt(s.getCreatedAt())
                .build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<SoinDTO>> getByPatient(@PathVariable Long patientId,
                                                        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        if (!patientRepository.existsByIdAndUtilisateurId(patientId, user.getId()))
            throw new AccessDeniedException("Accès refusé");
        return ResponseEntity.ok(soinRepository.findByPatientIdOrderByDateSoinDesc(patientId)
                .stream().map(this::toDTO).toList());
    }

    @PostMapping
    public ResponseEntity<SoinDTO> create(@Valid @RequestBody SoinDTO dto,
                                           @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        if (!patient.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");

        Soin soin = Soin.builder()
                .type(dto.getType()).description(dto.getDescription())
                .dateSoin(dto.getDateSoin()).notes(dto.getNotes())
                .moduleSpecifique(dto.getModuleSpecifique())
                .patient(patient).utilisateur(user)
                .build();

        if (dto.getModuleId() != null) {
            moduleRepository.findById(dto.getModuleId()).ifPresent(soin::setModule);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(soinRepository.save(soin)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Soin soin = soinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soin introuvable"));
        if (!soin.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");
        soinRepository.delete(soin);
        return ResponseEntity.noContent().build();
    }
}
