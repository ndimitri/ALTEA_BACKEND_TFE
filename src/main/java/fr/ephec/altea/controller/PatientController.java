package fr.ephec.altea.controller;

import fr.ephec.altea.dto.AddressDTO;
import fr.ephec.altea.dto.PatientDTO;
import fr.ephec.altea.entity.*;
import fr.ephec.altea.exception.*;
import fr.ephec.altea.service.PatientService;
import fr.ephec.altea.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/patients")
@Tag(name = "Patients")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private final PatientService patientService;
    private final UtilisateurService utilisateurService;

    public PatientController(PatientService patientService,
                              UtilisateurService utilisateurService) {
        this.patientService = patientService;
        this.utilisateurService = utilisateurService;
    }

    private Utilisateur getUser(UserDetails ud) {
        return utilisateurService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    private void checkOwnership(Patient patient, Utilisateur user) {
        if (!patient.getUtilisateur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Accès non autorisé à ce patient");
        }
    }

    private PatientDTO toDTO(Patient p) {
        return PatientDTO.builder()
                .id(p.getId()).nom(p.getNom()).prenom(p.getPrenom())
                .dateNaissance(p.getDateNaissance()).addressDTO(
                        AddressDTO.builder()
                                .houseNumber(p.getAdresse().getHouseNumber())
                                .road(p.getAdresse().getRoad())
                                .town(p.getAdresse().getTown())
                                .postcode(p.getAdresse().getPostcode())
                                .country(p.getAdresse().getCountry())
                                .build()
                )
                .latitude(p.getLatitude()).longitude(p.getLongitude())
                .telephone(p.getTelephone()).email(p.getEmail())
                .informationsMedicales(p.getInformationsMedicales()).notes(p.getNotes())
                .numeroMutuelle(p.getNumeroMutuelle()).nomMutuelle(p.getNomMutuelle())
                .medecinReferent(p.getMedecinReferent()).createdAt(p.getCreatedAt())
                .build();
    }

    @GetMapping
    @Operation(summary = "Liste des patients de l'utilisateur connecté")
    public ResponseEntity<List<PatientDTO>> getAll(@AuthenticationPrincipal UserDetails ud,
                                                    @RequestParam(required = false) String q) {
        Utilisateur user = getUser(ud);
        List<Patient> patients = (q != null && !q.isBlank())
                ? patientService.searchByNomOrPrenom(user.getId(), q)
                : patientService.findByUtilisateurIdOrderByNomAsc(user.getId());
        return ResponseEntity.ok(patients.stream().map(this::toDTO).toList());
    }

    @GetMapping("/map")
    @Operation(summary = "Patients avec coordonnées GPS pour la carte Leaflet")
    public ResponseEntity<List<PatientDTO>> getForMap(@AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        return ResponseEntity.ok(
                patientService.findWithCoordinatesByUtilisateurId(user.getId())
                        .stream().map(this::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getById(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Patient p = patientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + id + " introuvable"));
        checkOwnership(p, user);
        return ResponseEntity.ok(toDTO(p));
    }

    @PostMapping
    public ResponseEntity<PatientDTO> create(@Valid @RequestBody PatientDTO dto,
                                              @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);

        System.out.println("DTO : " + dto.getAddressDTO());

        Patient p = new Patient();
        applyDTO(p, dto);
        p.setUtilisateur(user);
        Patient saved = patientService.save(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody PatientDTO dto,
                                              @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Patient p = patientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        checkOwnership(p, user);
        applyDTO(p, dto);
        return ResponseEntity.ok(toDTO(patientService.save(p)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails ud) {
        Utilisateur user = getUser(ud);
        Patient p = patientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        checkOwnership(p, user);
        patientService.delete(p);
        return ResponseEntity.noContent().build();
    }

    private void applyDTO(Patient p, PatientDTO dto) {
        p.setNom(dto.getNom()); p.setPrenom(dto.getPrenom());
        p.setDateNaissance(dto.getDateNaissance()); p.setAdresse(AddressDTO.toEntity(dto.getAddressDTO()));
        p.setLatitude(dto.getLatitude()); p.setLongitude(dto.getLongitude());
        p.setTelephone(dto.getTelephone()); p.setEmail(dto.getEmail());
        p.setInformationsMedicales(dto.getInformationsMedicales()); p.setNotes(dto.getNotes());
        p.setNumeroMutuelle(dto.getNumeroMutuelle()); p.setNomMutuelle(dto.getNomMutuelle());
        p.setMedecinReferent(dto.getMedecinReferent());
    }
}
