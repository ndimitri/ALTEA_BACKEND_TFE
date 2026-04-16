package fr.ephec.altea.service;

import fr.ephec.altea.entity.SoinTemplate;
import java.util.List;
import java.util.Optional;

public interface SoinTemplateService {
    List<SoinTemplate> findByUtilisateurIdOrderByNomAsc(Long utilisateurId);
    List<SoinTemplate> findByUtilisateurIdAndModuleIdOrderByNomAsc(Long utilisateurId, Long moduleId);
    List<SoinTemplate> findByModuleIdOrderByNomAsc(Long moduleId);
    Optional<SoinTemplate> findById(Long id);
    SoinTemplate save(SoinTemplate soinTemplate);
    void delete(SoinTemplate soinTemplate);
}

