package fr.ephec.altea.service.impl;

import fr.ephec.altea.entity.RendezVous;
import fr.ephec.altea.repository.RendezVousRepository;
import fr.ephec.altea.service.RendezVousService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;

    public RendezVousServiceImpl(RendezVousRepository rendezVousRepository) {
        this.rendezVousRepository = rendezVousRepository;
    }

    @Override
    public List<RendezVous> findByUtilisateurIdOrderByDateHeureDebutAsc(Long utilisateurId) {
        return rendezVousRepository.findByUtilisateurIdOrderByDateHeureDebutAsc(utilisateurId);
    }

    @Override
    public long countByUtilisateurId(Long utilisateurId) {
        return rendezVousRepository.countByUtilisateurId(utilisateurId);
    }

    @Override
    public List<RendezVous> findByUtilisateurIdAndPeriode(Long utilisateurId, LocalDateTime debut, LocalDateTime fin) {
        return rendezVousRepository.findByUtilisateurIdAndPeriode(utilisateurId, debut, fin);
    }

    @Override
    public List<RendezVous> findConflicts(Long utilisateurId, LocalDateTime debut, LocalDateTime fin, Long excludeId) {
        return rendezVousRepository.findConflicts(utilisateurId, debut, fin, excludeId);
    }

//    @Override
//    public boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId) {
//        return rendezVousRepository.existsByIdAndUtilisateurId(id, utilisateurId);
//    }

    @Override
    public List<RendezVous> findByPatientIdOrderByDateHeureDebutDesc(Long patientId) {
        return rendezVousRepository.findByPatientIdOrderByDateHeureDebutDesc(patientId);
    }

    @Override
    public Optional<RendezVous> findById(Long id) {
        return rendezVousRepository.findById(id);
    }

    @Override
    public RendezVous save(RendezVous rendezVous) {
        return rendezVousRepository.save(rendezVous);
    }

    @Override
    public void delete(RendezVous rendezVous) {
        rendezVousRepository.delete(rendezVous);
    }
}

