package fr.ephec.altea.service.impl;

import fr.ephec.altea.entity.ModuleUtilisateur;
import fr.ephec.altea.repository.ModuleUtilisateurRepository;
import fr.ephec.altea.service.ModuleUtilisateurService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ModuleUtilisateurServiceImpl implements ModuleUtilisateurService {

    private final ModuleUtilisateurRepository moduleUtilisateurRepository;

    public ModuleUtilisateurServiceImpl(ModuleUtilisateurRepository moduleUtilisateurRepository) {
        this.moduleUtilisateurRepository = moduleUtilisateurRepository;
    }

    @Override
    public List<ModuleUtilisateur> findByUtilisateurIdAndActifTrue(Long utilisateurId) {
        return moduleUtilisateurRepository.findByUtilisateurIdAndActifTrue(utilisateurId);
    }

    @Override
    public Optional<ModuleUtilisateur> findByUtilisateurIdAndModuleId(Long utilisateurId, Long moduleId) {
        return moduleUtilisateurRepository.findByUtilisateurIdAndModuleId(utilisateurId, moduleId);
    }

//    @Override
//    public boolean existsByUtilisateurIdAndModuleIdAndActifTrue(Long utilisateurId, Long moduleId) {
//        return moduleUtilisateurRepository.existsByUtilisateurIdAndModuleIdAndActifTrue(utilisateurId, moduleId);
//    }

    @Override
    public ModuleUtilisateur save(ModuleUtilisateur moduleUtilisateur) {
        return moduleUtilisateurRepository.save(moduleUtilisateur);
    }
}

