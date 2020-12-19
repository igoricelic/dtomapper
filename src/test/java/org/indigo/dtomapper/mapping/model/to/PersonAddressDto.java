package org.indigo.dtomapper.mapping.model.to;

import lombok.Data;
import org.indigo.dtomapper.annotations.CustomMapping;
import org.indigo.dtomapper.annotations.Property;

@Data
public class PersonAddressDto {

    @Property(path = "address")
    @CustomMapping(function = "getFullAddress")
    private String fullAddress;

}
