package fr.ephec.altea.repository;

import fr.ephec.altea.entity.*;
import fr.ephec.altea.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// ==================== Utilisateur ====================
@Repository
interface UtilisateurRepositoryBase extends JpaRepository<Utilisateur, Long> {}

