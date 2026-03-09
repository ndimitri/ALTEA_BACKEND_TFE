package fr.ephec.altea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class    PatientDTO {
    private Long id;
    @NotBlank @Size(max = 100)
    private String nom;
    @NotBlank @Size(max = 100)
    private String prenom;
    @Past
    private LocalDate dateNaissance;
    @JsonProperty("address")
    private AddressDTO addressDTO;
    private Double latitude;
    private Double longitude;
    @Pattern(regexp = "^[+0-9 ()/-]{0,20}$", message = "Téléphone invalide")
    private String telephone;
    @Email
    private String email;
    private String informationsMedicales;
    private String notes;
    private String numeroMutuelle;
    private String nomMutuelle;
    private String medecinReferent;
    private LocalDateTime createdAt;
}
