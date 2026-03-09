package fr.ephec.altea.repository;

import fr.ephec.altea.entity.Soin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ==================== Soin ====================
@Repository
public interface SoinRepository extends JpaRepository<Soin, Long> {
    List<Soin> findByPatientIdOrderByDateSoinDesc(Long patientId);
    List<Soin> findByUtilisateurIdOrderByDateSoinDesc(Long utilisateurId);
    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);
}
