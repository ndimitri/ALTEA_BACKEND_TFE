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
    private final SoinTemplateRepository soinTemplateRepository;

    public DataInitializer(ModuleRepository moduleRepository,
                           UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder,
                           SoinTemplateRepository soinTemplateRepository) {
        this.moduleRepository = moduleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.soinTemplateRepository = soinTemplateRepository;
    }

    @Override
    public void run(String... args) {
        seedModules();
        seedAdminUser();
        seedSoinTemplates();
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

    private void seedSoinTemplates() {
        if (soinTemplateRepository.count() > 0) return;

        Utilisateur admin = utilisateurRepository.findByEmail("admin@altea.be")
                .orElse(null);
        if (admin == null) return;

        Module pedicure    = moduleRepository.findByNom("PEDICURE").orElse(null);
        Module infirmiere  = moduleRepository.findByNom("INFIRMIERE").orElse(null);
        Module kine        = moduleRepository.findByNom("KINE").orElse(null);
        Module aideSoignant = moduleRepository.findByNom("AIDE_SOIGNANT").orElse(null);

        List<SoinTemplate> templates = List.of(

            // --- PEDICURE ---
            SoinTemplate.builder()
                .nom("Soin ongle incarné")
                .type("Ongle incarné")
                .description("Traitement d'un ongle incarné : découpe et dépose de l'ongle, désinfection et pansement protecteur.")
                .notes("Vérifier état vasculaire avant intervention. Conseiller chaussures larges post-soin.")
                .module(pedicure).utilisateur(admin).build(),

            SoinTemplate.builder()
                .nom("Soin cors et durillons")
                .type("Cors / Durillons")
                .description("Ablation des cors et durillons à l'aide d'un bistouri ou d'une fraise. Application d'un kératolytique si nécessaire.")
                .notes("Hydratation quotidienne recommandée. Semelles orthopédiques à envisager si récidive.")
                .module(pedicure).utilisateur(admin).build(),

            // --- INFIRMIERE ---
            SoinTemplate.builder()
                .nom("Prise de sang standard")
                .type("Prise de sang")
                .description("Prélèvement veineux pour bilan sanguin standard (NFS, glycémie, CRP). Étiquetage et envoi au laboratoire.")
                .notes("Patient à jeun requis. Appliquer une légère pression post-prélèvement.")
                .module(infirmiere).utilisateur(admin).build(),

            SoinTemplate.builder()
                .nom("Pansement plaie chronique")
                .type("Pansement")
                .description("Nettoyage de la plaie au sérum physiologique, détersion si nécessaire, application d'un pansement adapté (hydrocolloïde, interface, alginate).")
                .notes("Photographier la plaie à chaque soin pour suivi évolution. Vérifier ordonnance du médecin.")
                .module(infirmiere).utilisateur(admin).build(),

            // --- KINE ---
            SoinTemplate.builder()
                .nom("Séance de rééducation genou")
                .type("Rééducation genou")
                .description("Travail de la mobilité articulaire du genou : étirements, renforcement quadriceps/ischio-jambiers, exercices proprioceptifs.")
                .notes("Évaluer EVA douleur avant/après. Adapter intensité selon tolérance du patient.")
                .module(kine).utilisateur(admin).build(),

            SoinTemplate.builder()
                .nom("Drainage lymphatique manuel")
                .type("Drainage lymphatique")
                .description("Drainage lymphatique manuel selon technique Vodder. Manœuvres douces et lentes sur les zones concernées.")
                .notes("Contre-indiqué en cas d'infection active ou de phlébite. Bandage compressif post-séance si prescrit.")
                .module(kine).utilisateur(admin).build(),

            // --- AIDE_SOIGNANT ---
            SoinTemplate.builder()
                .nom("Toilette complète au lit")
                .type("Toilette complète")
                .description("Toilette complète au lit : lavage du corps, soins bucco-dentaires, soins capillaires, habillage et réfection du lit.")
                .notes("Respecter la pudeur et l'intimité du patient. Surveiller état cutané (rougeurs, escarres).")
                .module(aideSoignant).utilisateur(admin).build(),

            SoinTemplate.builder()
                .nom("Aide à la prise du repas")
                .type("Aide alimentaire")
                .description("Installation confortable du patient, préparation du plateau repas, aide à la prise alimentaire et hydratation.")
                .notes("Vérifier régime alimentaire prescrit. Signaler tout trouble de déglutition à l'infirmière référente.")
                .module(aideSoignant).utilisateur(admin).build()
        );

        soinTemplateRepository.saveAll(templates);
        System.out.println("✅ Templates de soins initialisés : " + templates.size() + " templates créés (2 par module).");
    }
}
