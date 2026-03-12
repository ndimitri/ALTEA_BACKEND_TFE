package fr.ephec.altea.repository;

import fr.ephec.altea.entity.Soin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ==================== Soin ====================
@Repository
public interface SoinRepository extends JpaRepository<Soin, Long> {
    List<Soin> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<Soin> findByUtilisateurIdOrderByCreatedAtDesc(Long utilisateurId);
    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);
    List<Soin> findByRendezVousIdOrderByCreatedAtDesc(Long rdvId);
}
