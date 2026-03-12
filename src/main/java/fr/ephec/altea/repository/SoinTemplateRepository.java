package fr.ephec.altea.repository;

import fr.ephec.altea.entity.SoinTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SoinTemplateRepository extends JpaRepository<SoinTemplate, Long> {
    // Templates créés par un utilisateur
    List<SoinTemplate> findByUtilisateurIdOrderByNomAsc(Long utilisateurId);
    // Templates d'un utilisateur filtrés par module
    List<SoinTemplate> findByUtilisateurIdAndModuleIdOrderByNomAsc(Long utilisateurId, Long moduleId);
    // Templates globaux d'un module (pour afficher les templates du module actif)
    List<SoinTemplate> findByModuleIdOrderByNomAsc(Long moduleId);
}
