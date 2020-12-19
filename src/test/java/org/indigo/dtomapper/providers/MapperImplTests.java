package org.indigo.dtomapper.providers;

import org.indigo.dtomapper.exceptions.NoTransformPointException;
import org.indigo.dtomapper.mapping.model.from.Address;
import org.indigo.dtomapper.mapping.model.from.City;
import org.indigo.dtomapper.mapping.model.from.Country;
import org.indigo.dtomapper.mapping.model.from.Person;
import org.indigo.dtomapper.mapping.model.to.PersonAddressDto;
import org.indigo.dtomapper.providers.specification.Mapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapperImplTests {

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
    void registerFunctionTest() {
        Mapper mapper = MapperFactory.getMapper();
        assertThrows(NoTransformPointException.class, () -> mapper.map(person, PersonAddressDto.class));
        mapper.registerFunction("getFullAddress", MapperImplTests::getFullAddress);
        PersonAddressDto personAddressDto = mapper.map(person, PersonAddressDto.class);
        assertEquals("Sredacka 11", personAddressDto.getFullAddress());
    }

    public static String getFullAddress(Address address) {
        return String.format("%s %s", address.getStreet(), address.getHouseNumber());
    }

}
