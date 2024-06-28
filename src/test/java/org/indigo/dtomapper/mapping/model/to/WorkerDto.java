package org.indigo.dtomapper.mapping.model.to;

import lombok.Data;
import org.indigo.dtomapper.annotations.Property;

@Data
public class WorkerDto {

    private String id;

    private String city;

    @Property(path = "address")
    private String street;

    private B person;

}
