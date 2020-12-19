package org.indigo.dtomapper.mapping.model.to;

import lombok.Data;
import org.indigo.dtomapper.annotations.Property;

@Data
public class AddressDto {

    private String street;

    private int houseNumber;

    @Property(path = "city.name")
    private String city;

    @Property(path = "city.country", depth = 1)
    private CountryDto country;

}
