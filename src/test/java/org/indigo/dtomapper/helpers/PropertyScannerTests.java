package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.exceptions.NoAccessPointException;
import org.indigo.dtomapper.helpers.specification.PropertyScanner;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.mapping.model.to.PersonAddressDto;
import org.indigo.dtomapper.mapping.model.to.PersonWithErrorDto;
import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.indigo.dtomapper.metadata.enums.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PropertyScannerTests {

    private PropertyScanner propertyScanner;

    private AbstractPropertyEvaluator propertyEvaluator;

    @BeforeAll
    void setup() {
        final ReflectionHelper reflectionHelper = new ReflectionHelperImpl();
        propertyScanner = new PropertyScannerImpl(reflectionHelper);
        propertyEvaluator = (AbstractPropertyEvaluator) propertyScanner;
    }

    @Test
    void readMetadataTest() {
        List<PropertyMetadata> propertyMetadataList = propertyScanner.readMetadata(PersonAddressDto.class);
        assertTrue(propertyMetadataList.size() > 0);
        // property 1
        PropertyMetadata fieldMetadata1 = propertyMetadataList.get(0);
        assertEquals("address", fieldMetadata1.getPath().get(0));
        assertEquals(String.class, fieldMetadata1.getBaseType());
        assertEquals(0, fieldMetadata1.getDepth());
        assertNotNull(fieldMetadata1.getReference());
        assertNotNull(fieldMetadata1.getSetter());
        assertNotNull(fieldMetadata1.getCustomMappingMetadata());
        assertFalse(fieldMetadata1.isRoot());
        assertFalse(fieldMetadata1.getPath().isEmpty());
        assertFalse(fieldMetadata1.isArray());
        assertFalse(fieldMetadata1.isCollection());
        assertFalse(fieldMetadata1.isNested());
        assertFalse(fieldMetadata1.isEnum());
        assertNull(fieldMetadata1.getCollectionType());
        assertEquals(Direction.None, fieldMetadata1.getIgnoreDirection());
        // property 2
        PropertyMetadata fieldMetadata2 = propertyMetadataList.get(1);
        assertEquals(2, fieldMetadata2.getPath().size());
        assertEquals(long.class, fieldMetadata2.getBaseType());
        assertNotNull(fieldMetadata2.getReference());
        assertNotNull(fieldMetadata2.getSetter());
        assertNull(fieldMetadata2.getCustomMappingMetadata());
        assertFalse(fieldMetadata2.isRoot());
        assertFalse(fieldMetadata2.isArray());
        assertFalse(fieldMetadata2.isCollection());
        assertFalse(fieldMetadata2.isNested());
        assertFalse(fieldMetadata2.isEnum());
        assertNull(fieldMetadata2.getCollectionType());
        assertEquals(Direction.None, fieldMetadata2.getIgnoreDirection());
        // property 3
        PropertyMetadata fieldMetadata3 = propertyMetadataList.get(2);
        assertFalse(fieldMetadata3.getPath().isEmpty());
        assertEquals(String.class, fieldMetadata3.getBaseType());
        assertNotNull(fieldMetadata3.getCollectionType());
        assertEquals(List.class, fieldMetadata3.getCollectionType());
        assertNotNull(fieldMetadata3.getReference());
        assertNotNull(fieldMetadata3.getSetter());
        assertNull(fieldMetadata3.getCustomMappingMetadata());
        assertTrue(fieldMetadata3.isRoot());
        assertFalse(fieldMetadata3.isArray());
        assertTrue(fieldMetadata3.isCollection());
        assertTrue(fieldMetadata3.isNested());
        assertFalse(fieldMetadata3.isEnum());
        assertEquals(Direction.Incoming, fieldMetadata3.getIgnoreDirection());
        // read with exception
        assertThrows(NoAccessPointException.class, () -> propertyScanner.readMetadata(PersonWithErrorDto.class));
    }

    @Test
    void isValidPathTest() {
        assertTrue(propertyEvaluator.isValidPath("address.city"));
        assertFalse(propertyEvaluator.isValidPath(".user"));
        assertTrue(propertyEvaluator.isValidPath("#.user.email"));
        assertFalse(propertyEvaluator.isValidPath("#address..houseNumber"));
        assertTrue(propertyEvaluator.isValidPath("#country"));
        assertFalse(propertyEvaluator.isValidPath("#.user."));
        assertFalse(propertyEvaluator.isValidPath("country.#"));
        assertTrue(propertyEvaluator.isValidPath("address.city.country"));
        assertFalse(propertyEvaluator.isValidPath("#."));
        assertTrue(propertyEvaluator.isValidPath("#"));
    }

    @Test
    void parsePathExpressionTest() {
        assertThrows(IllegalStateException.class, () -> propertyEvaluator.parse("#address."));

        List<String> validPath1 = propertyEvaluator.parse("#user.address.city.name");
        assertEquals(4, validPath1.size());
        assertEquals("user", validPath1.get(0));

        List<String> validPath2 = propertyEvaluator.parse("user.email");
        assertEquals(2, validPath2.size());
        assertEquals("user", validPath2.get(0));

        List<String> validPath3 = propertyEvaluator.parse("#.user.id");
        assertEquals(2, validPath3.size());
        assertEquals("user", validPath3.get(0));
    }

    @Test
    void isRootPropertyTest() {
        assertTrue(propertyEvaluator.isRootProperty(Collections.singletonList("#")));
        assertFalse(propertyEvaluator.isRootProperty(Arrays.asList("address", "value")));
        assertFalse(propertyEvaluator.isRootProperty(Collections.singletonList("value")));
    }

}
