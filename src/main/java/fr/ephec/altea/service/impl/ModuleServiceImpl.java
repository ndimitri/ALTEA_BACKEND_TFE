package fr.ephec.altea.service.impl;

import fr.ephec.altea.entity.Module;
import fr.ephec.altea.repository.ModuleRepository;
import fr.ephec.altea.service.ModuleService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleServiceImpl(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }


    @Override
    public List<Module> findByActifTrue() {
        return moduleRepository.findByActifTrue();
    }

    @Override
    public Optional<Module> findById(Long id) {
        return moduleRepository.findById(id);
    }

    @Override
    public Module save(Module module) {
        return moduleRepository.save(module);
    }

//    @Override
//    public List<Module> findAll() {
//        return moduleRepository.findAll();
//    }
}

