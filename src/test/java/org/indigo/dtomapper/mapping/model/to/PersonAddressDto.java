package org.indigo.dtomapper.mapping.model.to;

import lombok.Data;
import org.indigo.dtomapper.annotations.CustomMapping;
import org.indigo.dtomapper.annotations.Ignore;
import org.indigo.dtomapper.annotations.Property;
import org.indigo.dtomapper.metadata.enums.Direction;

import java.util.List;

@Data
public class PersonAddressDto {

    private static String clazzId = "";

    private final Long id = 1L;

    @Property(path = "address")
    @CustomMapping(function = "getFullAddress")
    private String fullAddress;

    @Property(path = "#.address.houseNumber")
    private long houseNumber;

    @Property(path = "#")
    @Ignore(direction = Direction.Incoming)
    private List<String> custom;

    @Property
    @Ignore(direction = Direction.Incoming)
    private Long[] customNumber;

}
