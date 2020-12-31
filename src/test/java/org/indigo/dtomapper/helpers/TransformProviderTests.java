package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.helpers.specification.TransformationProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransformProviderTests {

    private TransformationProvider transformationProvider;

    @BeforeAll
    void setup() {
        transformationProvider = new TransformationProviderImpl();
    }

    @Test
    void findTransformationTest() {
        assertNotNull(transformationProvider.findTransformation(Long.class, Integer.class));
        assertNotNull(transformationProvider.findTransformation(Long.class, int.class));
        assertNotNull(transformationProvider.findTransformation(int.class, String.class));
        assertNull(transformationProvider.findTransformation(LocalDateTime.class, long.class));
        assertNull(transformationProvider.findTransformation(Date.class, Byte.class));
        assertNull(transformationProvider.findTransformation(List.class, Set.class));
    }

}
