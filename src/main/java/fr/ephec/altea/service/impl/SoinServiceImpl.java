package fr.ephec.altea.service.impl;

import fr.ephec.altea.entity.Soin;
import fr.ephec.altea.repository.SoinRepository;
import fr.ephec.altea.service.SoinService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SoinServiceImpl implements SoinService {

    private final SoinRepository soinRepository;

    public SoinServiceImpl(SoinRepository soinRepository) {
        this.soinRepository = soinRepository;
    }

    @Override
    public List<Soin> findByPatientIdOrderByCreatedAtDesc(Long patientId) {
        return soinRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }



//    @Override
//    public boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId) {
//        return soinRepository.existsByIdAndUtilisateurId(id, utilisateurId);
//    }

    @Override
    public List<Soin> findByRendezVousIdOrderByCreatedAtDesc(Long rdvId) {
        return soinRepository.findByRendezVousIdOrderByCreatedAtDesc(rdvId);
    }

    @Override
    public Optional<Soin> findById(Long id) {
        return soinRepository.findById(id);
    }

    @Override
    public Soin save(Soin soin) {
        return soinRepository.save(soin);
    }

    @Override
    public void delete(Soin soin) {
        soinRepository.delete(soin);
    }
}

