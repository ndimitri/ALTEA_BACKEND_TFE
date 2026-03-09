package fr.ephec.altea.repository;

import fr.ephec.altea.entity.Module;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ==================== Module ====================
@Repository
public interface ModuleRepository extends JpaRepository<fr.ephec.altea.entity.Module, Long> {
    Optional<fr.ephec.altea.entity.Module> findByNom(String nom);
    List<Module> findByActifTrue();
}
