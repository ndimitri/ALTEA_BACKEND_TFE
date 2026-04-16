package fr.ephec.altea.service;

import fr.ephec.altea.entity.Module;
import java.util.List;
import java.util.Optional;

public interface ModuleService {

    List<Module> findByActifTrue();
    Optional<Module> findById(Long id);
    Module save(Module module);
//    List<Module> findAll();
}

