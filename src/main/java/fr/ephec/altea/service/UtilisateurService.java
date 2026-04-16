package fr.ephec.altea.service;

import fr.ephec.altea.entity.Utilisateur;
import java.util.List;
import java.util.Optional;

public interface UtilisateurService {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utilisateur> findAll();
//    List<Utilisateur> findByActifTrue();
    Optional<Utilisateur> findById(Long id);
    Utilisateur save(Utilisateur utilisateur);
//    Utilisateur findByEmailOrThrow(String email);
    Utilisateur toggleUserStatus(Long id);
}

