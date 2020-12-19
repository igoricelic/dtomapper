package org.indigo.dtomapper.mapping.model.to;

import lombok.Data;
import org.indigo.dtomapper.annotations.Property;

import java.time.LocalDateTime;

@Data
public class SimplePersonDto {

    private int id;

    @Property(path = "firstName")
    private String name;

    @Property(path = "lastName")
    private String surname;

    @Property(path = "dateOfBirth")
    private LocalDateTime birthday;

    @Property(path = "address.city.name")
    private String city;

    @Property(path = "address.city.country.name", depth = 1)
    private String country;

    @Property(path = "#address.city.country", depth = 1)
    private CountryDto countryDto;

}
