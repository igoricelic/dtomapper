package org.indigo.dtomapper.mapping.model.to;

import lombok.Getter;
import org.indigo.dtomapper.annotations.Ignore;
import org.indigo.dtomapper.annotations.Property;
import org.indigo.dtomapper.metadata.enums.Direction;

@Getter
public class PersonWithErrorDto {

    @Property(path = "#", depth = 1)
    @Ignore(direction = Direction.Incoming)
    private String customText;

}
