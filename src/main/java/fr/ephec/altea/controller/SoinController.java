//package fr.ephec.altea.controller;
//
//import fr.ephec.altea.dto.SoinDTO;
//import fr.ephec.altea.entity.*;
//import fr.ephec.altea.exception.*;
//import fr.ephec.altea.repository.*;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import org.springframework.http.*;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/soins")
//@Tag(name = "Soins")
//@SecurityRequirement(name = "bearerAuth")
//public class SoinController {
//
//    private final SoinRepository soinRepository;
//    private final PatientRepository patientRepository;
//    private final UtilisateurRepository utilisateurRepository;
//    private final ModuleRepository moduleRepository;
//    private final RendezVousRepository rdvRepository;
//
//    public SoinController(SoinRepository soinRepository, PatientRepository patientRepository,
//                           UtilisateurRepository utilisateurRepository, ModuleRepository moduleRepository,RendezVousRepository rdvRepository) {
//
//        this.soinRepository = soinRepository;
//        this.patientRepository = patientRepository;
//        this.utilisateurRepository = utilisateurRepository;
//        this.moduleRepository = moduleRepository;
//        this.rdvRepository = rdvRepository;
//    }
//
//    private Utilisateur getUser(UserDetails ud) {
//        return utilisateurRepository.findByEmail(ud.getUsername())
//                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
//    }
//
//    private SoinDTO toDTO(Soin s) {
//        return SoinDTO.builder()
//                .id(s.getId()).type(s.getType()).description(s.getDescription())
//                .dateSoin(s.getDateSoin()).notes(s.getNotes())
//                .moduleSpecifique(s.getModuleSpecifique())
//                .patientId(s.getPatient().getId())
//                .patientNom(s.getPatient().getNom() + " " + s.getPatient().getPrenom())
//                .moduleId(s.getModule() != null ? s.getModule().getId() : null)
//                .moduleNom(s.getModule() != null ? s.getModule().getNom() : null)
//                .createdAt(s.getCreatedAt())
//                .build();
//    }
//
//    @GetMapping("/patient/{patientId}")
//    public ResponseEntity<List<SoinDTO>> getByPatient(@PathVariable Long patientId,
//                                                        @AuthenticationPrincipal UserDetails ud) {
//        Utilisateur user = getUser(ud);
//        if (!patientRepository.existsByIdAndUtilisateurId(patientId, user.getId()))
//            throw new AccessDeniedException("Accès refusé");
//        return ResponseEntity.ok(soinRepository.findByPatientIdOrderByDateSoinDesc(patientId)
//                .stream().map(this::toDTO).toList());
//    }
//
//    @PostMapping
//    public ResponseEntity<SoinDTO> create(@Valid @RequestBody SoinDTO dto,
//                                           @AuthenticationPrincipal UserDetails ud) {
//        Utilisateur user = getUser(ud);
//        Patient patient = patientRepository.findById(dto.getPatientId())
//                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
//        if (!patient.getUtilisateur().getId().equals(user.getId()))
//            throw new AccessDeniedException("Accès refusé");
//
//        Soin soin = Soin.builder()
//                .type(dto.getType()).description(dto.getDescription())
//                .dateSoin(dto.getDateSoin()).notes(dto.getNotes())
//                .moduleSpecifique(dto.getModuleSpecifique())
//                .patient(patient).utilisateur(user)
//                .build();
//
//        if (dto.getModuleId() != null) {
//            moduleRepository.findById(dto.getModuleId()).ifPresent(soin::setModule);
//        }
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(soinRepository.save(soin)));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id,
//                                        @AuthenticationPrincipal UserDetails ud) {
//        Utilisateur user = getUser(ud);
//        Soin soin = soinRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Soin introuvable"));
//        if (!soin.getUtilisateur().getId().equals(user.getId()))
//            throw new AccessDeniedException("Accès refusé");
//        soinRepository.delete(soin);
//        return ResponseEntity.noContent().build();
//    }
//}
package fr.ephec.altea.controller;

import fr.ephec.altea.dto.SoinDTO;
import fr.ephec.altea.entity.Module;
import fr.ephec.altea.entity.Patient;
import fr.ephec.altea.entity.RendezVous;
import fr.ephec.altea.entity.Soin;
import fr.ephec.altea.entity.Utilisateur;
import fr.ephec.altea.exception.ResourceNotFoundException;
import fr.ephec.altea.service.ModuleService;
import fr.ephec.altea.service.PatientService;
import fr.ephec.altea.service.RendezVousService;
import fr.ephec.altea.service.SoinService;
import fr.ephec.altea.service.UtilisateurService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/soins")
@Tag(name = "Soins")
@SecurityRequirement(name = "bearerAuth")
public class SoinController {

    private final SoinService soinService;
    private final PatientService patientService;
    private final UtilisateurService utilisateurService;
    private final ModuleService moduleService;
    private final RendezVousService rdvService;

    public SoinController(SoinService soinService,
                          PatientService patientService,
                          UtilisateurService utilisateurService,
                          ModuleService moduleService,
                          RendezVousService rdvService) {
        this.soinService = soinService;
        this.patientService = patientService;
        this.utilisateurService = utilisateurService;
        this.moduleService = moduleService;
        this.rdvService = rdvService;
    }

    private Utilisateur getUser(UserDetails ud) {
        return utilisateurService.findByEmail(ud.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    private SoinDTO toDTO(Soin s) {
        return SoinDTO.builder()
            .id(s.getId()).type(s.getType()).description(s.getDescription())
            .moduleSpecifique(s.getModuleSpecifique())
            .patientId(s.getPatient().getId())
            .patientNom(s.getPatient().getNom() + " " + s.getPatient().getPrenom())
            .moduleId(s.getModule() != null ? s.getModule().getId() : null)
            .moduleNom(s.getModule() != null ? s.getModule().getNom() : null)
            .rendezVousId(s.getRendezVous() != null ? s.getRendezVous().getId() : null)
            .createdAt(s.getCreatedAt())
            .build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<SoinDTO>> getByPatient(@PathVariable Long patientId,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        if (!patientService.existsByIdAndUtilisateurId(patientId, user.getId())) {
            throw new AccessDeniedException("Accès refusé");
        }
        return ResponseEntity.ok(soinService.findByPatientIdOrderByCreatedAtDesc(patientId)
            .stream().map(this::toDTO).toList());
    }

    @GetMapping("/rdv/{rdvId}")
    public ResponseEntity<List<SoinDTO>> getByRdv(@PathVariable Long rdvId,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous rdv = rdvService.findById(rdvId)
            .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!rdv.getUtilisateur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Accès refusé");
        }
        return ResponseEntity.ok(soinService.findByRendezVousIdOrderByCreatedAtDesc(rdvId)
            .stream().map(this::toDTO).toList());
    }

    @PostMapping
    public ResponseEntity<SoinDTO> create(@Valid @RequestBody SoinDTO dto,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Patient patient = patientService.findById(dto.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        if (!patient.getUtilisateur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Accès refusé");
        }

        Soin soin = Soin.builder()
            .type(dto.getType()).description(dto.getDescription())
            .notes(dto.getNotes())
            .moduleSpecifique(dto.getModuleSpecifique())
            .patient(patient).utilisateur(user)
            .build();

        if (dto.getModuleId() != null) {
            Module module = moduleService.findById(dto.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable"));
            soin.setModule(module);
        }

        if (dto.getRendezVousId() != null) {
            RendezVous rdv = rdvService.findById(dto.getRendezVousId())
                .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
            if (!rdv.getUtilisateur().getId().equals(user.getId())) {
                throw new AccessDeniedException("Accès refusé");
            }
            soin.setRendezVous(rdv);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(soinService.save(soin)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoinDTO> update(@PathVariable Long id,
        @Valid @RequestBody SoinDTO dto,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Soin soin = soinService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Soin introuvable"));
        if (!soin.getUtilisateur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Accès refusé");
        }

        soin.setType(dto.getType());
        if (dto.getDescription() != null) {
            soin.setDescription(dto.getDescription());
        }
        if (dto.getNotes() != null) {
            soin.setNotes(dto.getNotes());
        }
        if (dto.getModuleSpecifique() != null) {
            soin.setModuleSpecifique(dto.getModuleSpecifique());
        }

        if (dto.getModuleId() != null) {
            Module module = moduleService.findById(dto.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable"));
            soin.setModule(module);
        }

        if (dto.getRendezVousId() != null) {
            RendezVous rdv = rdvService.findById(dto.getRendezVousId())
                .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
            if (!rdv.getUtilisateur().getId().equals(user.getId())) {
                throw new AccessDeniedException("Accès refusé");
            }
            soin.setRendezVous(rdv);
        } else {
            soin.setRendezVous(null);
        }

        return ResponseEntity.ok(toDTO(soinService.save(soin)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Soin soin = soinService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Soin introuvable"));
        if (!soin.getUtilisateur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Accès refusé");
        }
        soinService.delete(soin);
        return ResponseEntity.noContent().build();
    }
}