package fr.ephec.altea.service;

import fr.ephec.altea.entity.Soin;
import java.util.List;
import java.util.Optional;

public interface SoinService {
    List<Soin> findByPatientIdOrderByCreatedAtDesc(Long patientId);

//    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);
    List<Soin> findByRendezVousIdOrderByCreatedAtDesc(Long rdvId);
    Optional<Soin> findById(Long id);
    Soin save(Soin soin);
    void delete(Soin soin);
}

