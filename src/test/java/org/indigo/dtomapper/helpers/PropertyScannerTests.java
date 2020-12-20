package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.helpers.specification.PropertyScanner;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.mapping.model.to.PersonAddressDto;
import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

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
        assertEquals(1, propertyMetadataList.size());
        PropertyMetadata metadata = propertyMetadataList.get(0);
        assertEquals("address", metadata.getPath().get(0));
        assertEquals(String.class, metadata.getBaseType());
        // todo:
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
        assertTrue(propertyEvaluator.isRootProperty(List.of("#")));
        assertFalse(propertyEvaluator.isRootProperty(List.of("address", "value")));
        assertFalse(propertyEvaluator.isRootProperty(List.of("value")));
    }

}
