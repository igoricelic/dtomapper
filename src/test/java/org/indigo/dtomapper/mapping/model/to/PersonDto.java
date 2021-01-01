package org.indigo.dtomapper.mapping.model.to;

import lombok.Data;
import org.indigo.dtomapper.annotations.CustomMapping;
import org.indigo.dtomapper.annotations.Property;

import java.util.Objects;

@Data
public class PersonDto {

    @Property(path = "id")
    private int identifier;

    private String firstName;

    @Property(depth = 2)
    private String lastName;

    @Property(path = "#dateOfBirth")
    @CustomMapping(clazz = Objects.class, function = "nonNull")
    private boolean hasBirthday;

    @Property(depth = 1)
    private AddressDto address;

    private String[] nicknames;

}
