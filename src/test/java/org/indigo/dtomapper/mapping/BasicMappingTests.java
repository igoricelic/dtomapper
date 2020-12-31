package org.indigo.dtomapper.mapping;

import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.mapping.model.from.Address;
import org.indigo.dtomapper.mapping.model.from.City;
import org.indigo.dtomapper.mapping.model.from.Country;
import org.indigo.dtomapper.mapping.model.from.Person;
import org.indigo.dtomapper.mapping.model.to.ErrorPersonDto;
import org.indigo.dtomapper.mapping.model.to.SimplePersonDto;
import org.indigo.dtomapper.providers.MapperFactory;
import org.indigo.dtomapper.providers.specification.Mapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BasicMappingTests {

    private final Mapper mapper = MapperFactory.getMapper();

    private Person person;

    @BeforeAll
    void setup(){
        person = Person.builder()
                .id(15L)
                .firstName("Pera")
                .lastName("Peric")
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
        SimplePersonDto personDto = mapper.map(person, SimplePersonDto.class);
        assertEquals(15, personDto.getId());
        assertEquals("Pera", personDto.getName());
        assertEquals("Peric", personDto.getSurname());
        assertNotNull(personDto.getBirthday());
        assertEquals("Belgrade", personDto.getCity());
        assertNull(personDto.getCountry());
        assertNull(personDto.getCountryDto());
        assertNotNull(personDto.getPerson());
    }

    @Test
    void secondLevelMappingTest() {
        SimplePersonDto personDto = mapper.map(person,1, SimplePersonDto.class);
        assertEquals("Serbia", personDto.getCountry());
        assertNotNull(personDto.getCountryDto());
        assertEquals("Serbia", personDto.getCountryDto().getName());
        assertEquals("rs", personDto.getCountryDto().getDomain());
    }

    @Test
    void mappingWithErrorTest() {
        assertThrows(IllegalStateException.class, () -> mapper.map(null, ErrorPersonDto.class));
        assertThrows(IllegalStateException.class, () -> mapper.map(person, ErrorPersonDto.class));
    }

}
