package fr.ephec.altea.repository;

import fr.ephec.altea.entity.RendezVous;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// ==================== RendezVous ====================
@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findByUtilisateurIdOrderByDateHeureDebutAsc(Long utilisateurId);

    @Query("SELECT r FROM RendezVous r WHERE r.utilisateur.id = :uid " +
           "AND r.dateHeureDebut >= :debut AND r.dateHeureFin <= :fin " +
           "ORDER BY r.dateHeureDebut ASC")
    List<RendezVous> findByUtilisateurIdAndPeriode(
            @Param("uid") Long utilisateurId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT r FROM RendezVous r WHERE r.utilisateur.id = :uid " +
           "AND r.dateHeureDebut < :fin AND r.dateHeureFin > :debut " +
           "AND (:excludeId IS NULL OR r.id != :excludeId)")
    List<RendezVous> findConflicts(
            @Param("uid") Long utilisateurId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("excludeId") Long excludeId);

    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);
}
