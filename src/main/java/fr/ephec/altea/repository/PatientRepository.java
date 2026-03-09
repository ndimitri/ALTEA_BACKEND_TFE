package fr.ephec.altea.repository;

import fr.ephec.altea.entity.Patient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// ==================== Patient ====================
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByUtilisateurId(Long utilisateurId);
    List<Patient> findByUtilisateurIdOrderByNomAsc(Long utilisateurId);

    @Query("SELECT p FROM Patient p WHERE p.utilisateur.id = :uid " +
           "AND (LOWER(p.nom) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(p.prenom) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Patient> searchByNomOrPrenom(@Param("uid") Long utilisateurId, @Param("q") String query);

    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);

    @Query("SELECT p FROM Patient p WHERE p.utilisateur.id = :uid AND p.latitude IS NOT NULL AND p.longitude IS NOT NULL")
    List<Patient> findWithCoordinatesByUtilisateurId(@Param("uid") Long utilisateurId);
}
