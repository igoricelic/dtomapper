package org.indigo.dtomapper.mapping;

import org.indigo.dtomapper.mapping.model.from.Address;
import org.indigo.dtomapper.mapping.model.from.City;
import org.indigo.dtomapper.mapping.model.from.Country;
import org.indigo.dtomapper.mapping.model.from.Person;
import org.indigo.dtomapper.mapping.model.to.PersonDto;
import org.indigo.dtomapper.providers.MapperFactory;
import org.indigo.dtomapper.providers.specification.Mapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvancedMappingTests {

    private final Mapper mapper = MapperFactory.getMapper();

    private Person person;

    @BeforeAll
    void setup(){
        person = Person.builder()
                .id(15L)
                .firstName("Pera")
                .lastName("Peric")
                .nicknames(Set.of())
                .dateOfBirth(LocalDateTime.now())
                .address(
                        Address.builder().street("Sredacka").houseNumber(11)
                                .city(
                                        City.builder().name("Belgrade").country(Country.builder().name("Serbia").domain("rs").build()).build()
                                )
                                .build()
                )
                .build();
    }

    @Test
    void firstLevelMappingTest() {
        PersonDto personDto = mapper.map(person, PersonDto.class);
        assertEquals(15, personDto.getIdentifier());
        assertEquals("Pera", personDto.getFirstName());
        assertNull(personDto.getLastName());
        assertTrue(personDto.isHasBirthday());
        assertNull(personDto.getAddress());
        assertNotNull(personDto.getNicknames());
        assertEquals(0, personDto.getNicknames().length);
    }

    @Test
    void secondLevelMappingTest() {
        PersonDto personDto = mapper.map(person,1, PersonDto.class);
        assertNull(personDto.getLastName());
        assertNotNull(personDto.getAddress());
        assertEquals("Belgrade", personDto.getAddress().getCity());
        assertNull(personDto.getAddress().getCountry());
    }

    @Test
    void thirdLevelMappingTest() {
        PersonDto personDto = mapper.map(person,2, PersonDto.class);
        assertEquals("Peric", personDto.getLastName());
        assertNotNull(personDto.getAddress().getCountry());
        assertEquals("rs", personDto.getAddress().getCountry().getDomain());
    }

}
