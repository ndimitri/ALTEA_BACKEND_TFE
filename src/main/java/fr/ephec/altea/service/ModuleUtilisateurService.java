package fr.ephec.altea.service;

import fr.ephec.altea.entity.ModuleUtilisateur;
import java.util.List;
import java.util.Optional;

public interface ModuleUtilisateurService {
    List<ModuleUtilisateur> findByUtilisateurIdAndActifTrue(Long utilisateurId);
    Optional<ModuleUtilisateur> findByUtilisateurIdAndModuleId(Long utilisateurId, Long moduleId);
//    boolean existsByUtilisateurIdAndModuleIdAndActifTrue(Long utilisateurId, Long moduleId);
    ModuleUtilisateur save(ModuleUtilisateur moduleUtilisateur);
}

