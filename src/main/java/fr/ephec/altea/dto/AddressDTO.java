package fr.ephec.altea.dto;


import fr.ephec.altea.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDTO {
  private String houseNumber;
  private String road;
  private String town;
  private String region;
  private String county;
  private String postcode;
  private String country;
  private String countryCode;

  public static Address toEntity(AddressDTO dto) {
    if (dto == null) return null;
    return Address.builder()
            .houseNumber(dto.getHouseNumber())
            .road(dto.getRoad())
            .town(dto.getTown())
            .postcode(dto.getPostcode())
            .country(dto.getCountry())
        .region(dto.getRegion())
        .county(dto.getCounty())
        .countryCode(dto.getCountryCode())
            .build();
  }
}
