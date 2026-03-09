package fr.ephec.altea.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateUtilisateurRequest {
    @NotBlank @Size(max = 100)
    private String nom;
    @NotBlank @Size(max = 100)
    private String prenom;
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    private String password;
    private String telephone;
}
