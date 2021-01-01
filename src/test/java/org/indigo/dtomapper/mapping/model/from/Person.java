package org.indigo.dtomapper.mapping.model.from;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private Long id;

    private String firstName;

    private String lastName;

    private LocalDateTime dateOfBirth;

    private Address address;

    private Set<String> nicknames;

}
