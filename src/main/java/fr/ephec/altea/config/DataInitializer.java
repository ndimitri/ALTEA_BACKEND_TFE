package fr.ephec.altea.config;

import fr.ephec.altea.entity.*;
import fr.ephec.altea.entity.Module;
import fr.ephec.altea.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ModuleRepository moduleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ModuleRepository moduleRepository,
                           UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder) {
        this.moduleRepository = moduleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedModules();
        seedAdminUser();
    }

    private void seedModules() {
        if (moduleRepository.count() > 0) return;

        List<Module> modules = List.of(
            Module.builder().nom("PEDICURE")
                .description("Module pédicure-podologue : suivi des soins podologiques, photos avant/après, gestion des mutuelles, fiches pathologies").build(),
            Module.builder().nom("INFIRMIERE")
                .description("Module infirmier(ère) : traitements, injections, stocks de matériel médical, feuilles de soins").build(),
            Module.builder().nom("KINE")
                .description("Module kinésithérapeute : programme d'exercices personnalisé, suivi de la progression, bilans fonctionnels").build(),
            Module.builder().nom("AIDE_SOIGNANT")
                .description("Module aide-soignant(e) : tâches de nursing, toilette, transmissions infirmières, tâches journalières").build()
        );

        moduleRepository.saveAll(modules);
        System.out.println("✅ Modules ALTEA initialisés : " + modules.size() + " modules créés.");
    }

    private void seedAdminUser() {
        if (utilisateurRepository.existsByEmail("admin@altea.be")) return;

        Utilisateur admin = Utilisateur.builder()
            .nom("Administrateur")
            .prenom("ALTEA")
            .email("admin@altea.be")
            .motDePasse(passwordEncoder.encode("Admin1234!"))
            .role(Role.ROLE_ADMIN)
            .actif(true)
            .build();

        utilisateurRepository.save(admin);
        System.out.println("✅ Compte admin créé : admin@altea.be / Admin1234!");
    }
}
