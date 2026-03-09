package fr.ephec.altea.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

  @JsonProperty("house_number")
  private String houseNumber;

  @JsonProperty("road")
  private String road;

  @JsonProperty("town")
  private String town;

  @JsonProperty("county")
  private String county;

  @JsonProperty("region")
  private String region;

  @JsonProperty("postcode")
  private String postcode;

  @JsonProperty("country")
  private String country;

  @JsonProperty("country_code")
  private String countryCode;

}
