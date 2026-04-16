package fr.ephec.altea.service.impl;

import fr.ephec.altea.entity.SoinTemplate;
import fr.ephec.altea.repository.SoinTemplateRepository;
import fr.ephec.altea.service.SoinTemplateService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SoinTemplateServiceImpl implements SoinTemplateService {

    private final SoinTemplateRepository soinTemplateRepository;

    public SoinTemplateServiceImpl(SoinTemplateRepository soinTemplateRepository) {
        this.soinTemplateRepository = soinTemplateRepository;
    }

    @Override
    public List<SoinTemplate> findByUtilisateurIdOrderByNomAsc(Long utilisateurId) {
        return soinTemplateRepository.findByUtilisateurIdOrderByNomAsc(utilisateurId);
    }

    @Override
    public List<SoinTemplate> findByUtilisateurIdAndModuleIdOrderByNomAsc(Long utilisateurId, Long moduleId) {
        return soinTemplateRepository.findByUtilisateurIdAndModuleIdOrderByNomAsc(utilisateurId, moduleId);
    }

    @Override
    public List<SoinTemplate> findByModuleIdOrderByNomAsc(Long moduleId) {
        return soinTemplateRepository.findByModuleIdOrderByNomAsc(moduleId);
    }

    @Override
    public Optional<SoinTemplate> findById(Long id) {
        return soinTemplateRepository.findById(id);
    }

    @Override
    public SoinTemplate save(SoinTemplate soinTemplate) {
        return soinTemplateRepository.save(soinTemplate);
    }

    @Override
    public void delete(SoinTemplate soinTemplate) {
        soinTemplateRepository.delete(soinTemplate);
    }
}

