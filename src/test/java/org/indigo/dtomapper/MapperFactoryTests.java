package org.indigo.dtomapper;

import org.indigo.dtomapper.providers.MapperFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapperFactoryTests {

    @Test
    void getMapperTest() {
        assertDoesNotThrow(MapperFactory::getMapper);
        assertNotNull(MapperFactory.getMapper());
    }

}
