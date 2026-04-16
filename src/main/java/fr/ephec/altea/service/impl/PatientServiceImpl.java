package fr.ephec.altea.service.impl;

import fr.ephec.altea.entity.Patient;
import fr.ephec.altea.repository.PatientRepository;
import fr.ephec.altea.service.PatientService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }


    @Override
    public List<Patient> findByUtilisateurIdOrderByNomAsc(Long utilisateurId) {
        return patientRepository.findByUtilisateurIdOrderByNomAsc(utilisateurId);
    }

    @Override
    public long countByUtilisateurId(Long utilisateurId) {
        return patientRepository.countByUtilisateurId(utilisateurId);
    }

    @Override
    public List<Patient> searchByNomOrPrenom(Long utilisateurId, String query) {
        return patientRepository.searchByNomOrPrenom(utilisateurId, query);
    }

    @Override
    public boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId) {
        return patientRepository.existsByIdAndUtilisateurId(id, utilisateurId);
    }

    @Override
    public List<Patient> findWithCoordinatesByUtilisateurId(Long utilisateurId) {
        return patientRepository.findWithCoordinatesByUtilisateurId(utilisateurId);
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public void delete(Patient patient) {
        patientRepository.delete(patient);
    }
}

