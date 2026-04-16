package fr.ephec.altea.service;

import fr.ephec.altea.entity.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    List<Patient> findByUtilisateurIdOrderByNomAsc(Long utilisateurId);
    long countByUtilisateurId(Long utilisateurId);
    List<Patient> searchByNomOrPrenom(Long utilisateurId, String query);
    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);
    List<Patient> findWithCoordinatesByUtilisateurId(Long utilisateurId);
    Optional<Patient> findById(Long id);
    Patient save(Patient patient);
    void delete(Patient patient);
}

