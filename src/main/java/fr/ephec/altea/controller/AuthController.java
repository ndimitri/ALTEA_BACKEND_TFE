package fr.ephec.altea.controller;

import fr.ephec.altea.dto.*;
import fr.ephec.altea.entity.*;
import fr.ephec.altea.security.JwtUtils;
import fr.ephec.altea.service.UtilisateurService;
import fr.ephec.altea.service.ModuleUtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UtilisateurService utilisateurService;
    private final ModuleUtilisateurService moduleUtilisateurService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils,
                          UtilisateurService utilisateurService,
                          ModuleUtilisateurService moduleUtilisateurService,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.utilisateurService = utilisateurService;
        this.moduleUtilisateurService = moduleUtilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur, retourne un JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtUtils.generateToken(request.getEmail());

        Utilisateur user = utilisateurService.findByEmail(request.getEmail()).orElseThrow();

        List<String> modules = moduleUtilisateurService
                .findByUtilisateurIdAndActifTrue(user.getId())
                .stream()
                .map(mu -> mu.getModule().getNom())
                .toList();

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .modulesActifs(modules)
                .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreateUtilisateurRequest request) {
        if (utilisateurService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Utilisateur user = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getPassword()))
                .telephone(request.getTelephone())
                .role(Role.ROLE_USER)
                .actif(true)
                .build();

        utilisateurService.save(user);

        String token = jwtUtils.generateToken(user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .modulesActifs(List.of())
                .build());
    }
}
