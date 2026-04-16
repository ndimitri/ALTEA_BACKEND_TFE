package fr.ephec.altea.service;

import fr.ephec.altea.entity.RendezVous;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RendezVousService {
    List<RendezVous> findByUtilisateurIdOrderByDateHeureDebutAsc(Long utilisateurId);
    long countByUtilisateurId(Long utilisateurId);
    List<RendezVous> findByUtilisateurIdAndPeriode(Long utilisateurId, LocalDateTime debut, LocalDateTime fin);
    List<RendezVous> findConflicts(Long utilisateurId, LocalDateTime debut, LocalDateTime fin, Long excludeId);
//    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);
    List<RendezVous> findByPatientIdOrderByDateHeureDebutDesc(Long patientId);
    Optional<RendezVous> findById(Long id);
    RendezVous save(RendezVous rendezVous);
    void delete(RendezVous rendezVous);
}

