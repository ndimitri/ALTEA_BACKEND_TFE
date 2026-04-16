package fr.ephec.altea.controller;

import fr.ephec.altea.dto.*;
import fr.ephec.altea.entity.*;
import fr.ephec.altea.exception.*;
import fr.ephec.altea.service.RendezVousService;
import fr.ephec.altea.service.PatientService;
import fr.ephec.altea.service.UtilisateurService;
import fr.ephec.altea.service.SoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rendezvous")
@Tag(name = "Rendez-vous & Calendrier")
@SecurityRequirement(name = "bearerAuth")
public class RendezVousController {

    private final RendezVousService rdvService;
    private final PatientService patientService;
    private final UtilisateurService utilisateurService;
    private final SoinService soinService;

    public RendezVousController(RendezVousService rdvService,
        PatientService patientService,
        UtilisateurService utilisateurService,
        SoinService soinService) {
        this.rdvService = rdvService;
        this.patientService = patientService;
        this.utilisateurService = utilisateurService;
        this.soinService = soinService;
    }

    private Utilisateur getUser(UserDetails ud) {
        return utilisateurService.findByEmail(ud.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    private SoinDTO toSoinDTO(Soin s) {
        return SoinDTO.builder()
            .id(s.getId()).type(s.getType())
            .description(s.getDescription())
            .notes(s.getNotes())
            .rendezVousId(s.getRendezVous() != null ? s.getRendezVous().getId() : null)
            .build();
    }

    private RendezVousDTO toDTO(RendezVous r) {
        return RendezVousDTO.builder()
            .id(r.getId())
            .dateHeureDebut(r.getDateHeureDebut())
            .dateHeureFin(r.getDateHeureFin())
            .lieu(r.getLieu())
            .commentaire(r.getCommentaire())
            .statut(r.getStatut().name())
            .couleur(r.getCouleur())
            .patientId(r.getPatient().getId())
            .patientNom(r.getPatient().getNom())
            .patientPrenom(r.getPatient().getPrenom())
            .patientAdresse(r.getPatient().getAdresse())
            .soins(r.getSoins().stream().map(this::toSoinDTO).toList())
            .createdAt(r.getCreatedAt())
            .build();
    }

    private String formatLieu(RendezVous r) {
        // Priorité : lieu du RDV, sinon adresse du patient, sinon null
        fr.ephec.altea.entity.Address a = r.getLieu() != null ? r.getLieu()
            : (r.getPatient() != null ? r.getPatient().getAdresse() : null);
        if (a == null) return null;
        StringBuilder sb = new StringBuilder();
        if (a.getRoad() != null)        sb.append(a.getRoad()).append(" ");
        if (a.getHouseNumber() != null) sb.append(a.getHouseNumber()).append(", ");
        if (a.getPostcode() != null)    sb.append(a.getPostcode()).append(" ");
        if (a.getTown() != null)        sb.append(a.getTown());
        else if (a.getCounty() != null) sb.append(a.getCounty());
        return sb.toString().trim().replaceAll(", $", "");
    }

    private CalendarEventDTO toCalendarEvent(RendezVous r) {
        return CalendarEventDTO.builder()
            .id(r.getId())
            .title(r.getPatient().getPrenom() + " " + r.getPatient().getNom())
            .start(r.getDateHeureDebut().toString())
            .end(r.getDateHeureFin().toString())
            .color(r.getCouleur())
            .allDay(false)
            .extendedProps(CalendarEventDTO.CalendarEventExtendedProps.builder()
                .patientId(r.getPatient().getId())
                .patientNom(r.getPatient().getNom() + " " + r.getPatient().getPrenom())
                .lieu(formatLieu(r))
                .statut(r.getStatut().name())
                .commentaire(r.getCommentaire())
                .build())
            .build();
    }

    @GetMapping
    @Operation(summary = "Liste des rendez-vous de l'utilisateur")
    public ResponseEntity<List<RendezVousDTO>> getAll(@AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        return ResponseEntity.ok(rdvService
            .findByUtilisateurIdOrderByDateHeureDebutAsc(user.getId())
            .stream().map(this::toDTO).toList());
    }

    @GetMapping("/calendar")
    @Operation(summary = "Rendez-vous au format FullCalendar")
    public ResponseEntity<List<CalendarEventDTO>> getForCalendar(
        @AuthenticationPrincipal UserDetails ud,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Utilisateur user = getUser(ud);
        List<RendezVous> rdvs;
        if (start != null && end != null) {
            rdvs = rdvService.findByUtilisateurIdAndPeriode(user.getId(), start, end);
        } else {
            rdvs = rdvService.findByUtilisateurIdOrderByDateHeureDebutAsc(user.getId());
        }
        return ResponseEntity.ok(rdvs.stream().map(this::toCalendarEvent).toList());
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Historique des RDV d'un patient, du plus récent au plus ancien")
    public ResponseEntity<List<RendezVousDTO>> getByPatient(
        @PathVariable Long patientId,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Patient patient = patientService.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        if (!patient.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");
        return ResponseEntity.ok(rdvService
            .findByPatientIdOrderByDateHeureDebutDesc(patientId)
            .stream().map(this::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RendezVousDTO> getById(@PathVariable Long id,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous r = rdvService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!r.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");
        return ResponseEntity.ok(toDTO(r));
    }

    @PostMapping
    public ResponseEntity<RendezVousDTO> create(@Valid @RequestBody RendezVousDTO dto,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        List<RendezVous> conflicts = rdvService.findConflicts(
            user.getId(), dto.getDateHeureDebut(), dto.getDateHeureFin(), null);
        if (!conflicts.isEmpty())
            throw new ConflictException("Conflit d'horaire avec un rendez-vous existant");

        Patient patient = patientService.findById(dto.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));

        RendezVous rdv = RendezVous.builder()
            .dateHeureDebut(dto.getDateHeureDebut())
            .dateHeureFin(dto.getDateHeureFin())
            .lieu(dto.getLieu() != null ? dto.getLieu() : patient.getAdresse())
            .commentaire(dto.getCommentaire())
            .couleur(dto.getCouleur() != null ? dto.getCouleur() : "#1F5C8B")
            .utilisateur(user)
            .patient(patient)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(rdvService.save(rdv)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RendezVousDTO> update(@PathVariable Long id,
        @Valid @RequestBody RendezVousDTO dto,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous rdv = rdvService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!rdv.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");

        List<RendezVous> conflicts = rdvService.findConflicts(
            user.getId(), dto.getDateHeureDebut(), dto.getDateHeureFin(), id);
        if (!conflicts.isEmpty())
            throw new ConflictException("Conflit d'horaire avec un rendez-vous existant");

        rdv.setDateHeureDebut(dto.getDateHeureDebut());
        rdv.setDateHeureFin(dto.getDateHeureFin());
        rdv.setLieu(dto.getLieu());
        rdv.setCommentaire(dto.getCommentaire());
        if (dto.getCouleur() != null) rdv.setCouleur(dto.getCouleur());
        if (dto.getStatut() != null) rdv.setStatut(RendezVous.StatutRdv.valueOf(dto.getStatut()));

        return ResponseEntity.ok(toDTO(rdvService.save(rdv)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous rdv = rdvService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!rdv.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");
        rdvService.delete(rdv);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/soins")
    public ResponseEntity<SoinDTO> addSoinToRdv(@PathVariable Long id,
        @Valid @RequestBody SoinDTO dto,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous rdv = rdvService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!rdv.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");

        // Si le soin a déjà un id → on le lie simplement au RDV sans le recréer
        if (dto.getId() != null) {
            Soin existing = soinService.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Soin introuvable"));
            if (!existing.getUtilisateur().getId().equals(user.getId()))
                throw new AccessDeniedException("Accès refusé");
            existing.setRendezVous(rdv);
            return ResponseEntity.ok(toSoinDTO(soinService.save(existing)));
        }

        // Sinon on crée un nouveau soin
        Soin soin = Soin.builder()
            .type(dto.getType()).description(dto.getDescription())
            .notes(dto.getNotes())
            .patient(rdv.getPatient()).utilisateur(user).rendezVous(rdv)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toSoinDTO(soinService.save(soin)));
    }

    @DeleteMapping("/{id}/soins/{soinId}")
    public ResponseEntity<Void> removeSoinFromRdv(@PathVariable Long id,
        @PathVariable Long soinId,
        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous rdv = rdvService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!rdv.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");
        Soin soin = soinService.findById(soinId)
            .orElseThrow(() -> new ResourceNotFoundException("Soin introuvable"));
        soin.setRendezVous(null);
        soinService.save(soin);
        return ResponseEntity.noContent().build();
    }
}
