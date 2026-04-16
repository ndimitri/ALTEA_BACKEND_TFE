package fr.ephec.altea.service.impl;

import fr.ephec.altea.entity.Utilisateur;
import fr.ephec.altea.exception.ResourceNotFoundException;
import fr.ephec.altea.repository.UtilisateurRepository;
import fr.ephec.altea.service.UtilisateurService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    @Override
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

//    @Override
//    public List<Utilisateur> findByActifTrue() {
//        return utilisateurRepository.findByActifTrue();
//    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

//    @Override
//    public Utilisateur findByEmailOrThrow(String email) {
//        return utilisateurRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
//    }

    @Override
    public Utilisateur toggleUserStatus(Long id) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setActif(!user.getActif());
        return utilisateurRepository.save(user);
    }
}

