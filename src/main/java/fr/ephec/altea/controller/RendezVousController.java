package fr.ephec.altea.controller;

import fr.ephec.altea.dto.*;
import fr.ephec.altea.entity.*;
import fr.ephec.altea.exception.*;
import fr.ephec.altea.repository.*;
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

    private final RendezVousRepository rdvRepository;
    private final PatientRepository patientRepository;
    private final UtilisateurRepository utilisateurRepository;

    public RendezVousController(RendezVousRepository rdvRepository,
                                 PatientRepository patientRepository,
                                 UtilisateurRepository utilisateurRepository) {
        this.rdvRepository = rdvRepository;
        this.patientRepository = patientRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    private Utilisateur getUser(UserDetails ud) {
        return utilisateurRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
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
                .createdAt(r.getCreatedAt())
                .build();
    }

    // FullCalendar format
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
                        .lieu(r.getLieu().getRoad() + " " + r.getLieu().getHouseNumber() + ", "
                            + r.getLieu().getTown())
                        .statut(r.getStatut().name())
                        .commentaire(r.getCommentaire())
                        .build())
                .build();
    }

    @GetMapping
    @Operation(summary = "Liste des rendez-vous de l'utilisateur")
    public ResponseEntity<List<RendezVousDTO>> getAll(@AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        return ResponseEntity.ok(rdvRepository
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
            rdvs = rdvRepository.findByUtilisateurIdAndPeriode(user.getId(), start, end);
        } else {
            rdvs = rdvRepository.findByUtilisateurIdOrderByDateHeureDebutAsc(user.getId());
        }

        return ResponseEntity.ok(rdvs.stream().map(this::toCalendarEvent).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RendezVousDTO> getById(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous r = rdvRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!r.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");
        return ResponseEntity.ok(toDTO(r));
    }

    @PostMapping
    public ResponseEntity<RendezVousDTO> create(@Valid @RequestBody RendezVousDTO dto,
                                                  @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);

        // Vérification conflit
        List<RendezVous> conflicts = rdvRepository.findConflicts(
                user.getId(), dto.getDateHeureDebut(), dto.getDateHeureFin(), null);
        if (!conflicts.isEmpty()) {
            throw new ConflictException("Conflit d'horaire avec un rendez-vous existant");
        }

        Patient patient = patientRepository.findById(dto.getPatientId())
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

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(rdvRepository.save(rdv)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RendezVousDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody RendezVousDTO dto,
                                                  @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!rdv.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");

        // Vérif conflit en excluant ce RDV
        List<RendezVous> conflicts = rdvRepository.findConflicts(
                user.getId(), dto.getDateHeureDebut(), dto.getDateHeureFin(), id);
        if (!conflicts.isEmpty()) {
            throw new ConflictException("Conflit d'horaire avec un rendez-vous existant");
        }

        rdv.setDateHeureDebut(dto.getDateHeureDebut());
        rdv.setDateHeureFin(dto.getDateHeureFin());
        rdv.setLieu(dto.getLieu());
        rdv.setCommentaire(dto.getCommentaire());
        if (dto.getCouleur() != null) rdv.setCouleur(dto.getCouleur());
        if (dto.getStatut() != null) rdv.setStatut(RendezVous.StatutRdv.valueOf(dto.getStatut()));

        return ResponseEntity.ok(toDTO(rdvRepository.save(rdv)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        RendezVous rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RDV introuvable"));
        if (!rdv.getUtilisateur().getId().equals(user.getId()))
            throw new AccessDeniedException("Accès refusé");
        rdvRepository.delete(rdv);
        return ResponseEntity.noContent().build();
    }
}
