package fr.ephec.altea.repository;

import fr.ephec.altea.entity.ModuleUtilisateur;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ==================== ModuleUtilisateur ====================
@Repository
public interface ModuleUtilisateurRepository extends JpaRepository<ModuleUtilisateur, Long> {
    List<ModuleUtilisateur> findByUtilisateurIdAndActifTrue(Long utilisateurId);
    Optional<ModuleUtilisateur> findByUtilisateurIdAndModuleId(Long utilisateurId, Long moduleId);
    boolean existsByUtilisateurIdAndModuleIdAndActifTrue(Long utilisateurId, Long moduleId);
}
