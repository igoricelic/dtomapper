package org.indigo.dtomapper.helpers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollectionHelperTests {

    private Collection<?> nullCollection, emptyCollection, notEmptyCollection;

    @BeforeAll
    void setup() {
        nullCollection = null;
        emptyCollection = new ArrayList<>();
        notEmptyCollection = Arrays.asList(1, 2, 3);
    }

    @Test
    void isEmptyTest() {
        assertTrue(CollectionHelper.isEmpty(nullCollection));
        assertTrue(CollectionHelper.isEmpty(emptyCollection));
        assertFalse(CollectionHelper.isEmpty(notEmptyCollection));
    }

    @Test
    void isNotEmptyTest() {
        assertFalse(CollectionHelper.isNotEmpty(nullCollection));
        assertFalse(CollectionHelper.isNotEmpty(emptyCollection));
        assertTrue(CollectionHelper.isNotEmpty(notEmptyCollection));
    }

    @Test
    void getFirstTest() {
        assertNull(CollectionHelper.getFirst(nullCollection));
        assertNull(CollectionHelper.getFirst(emptyCollection));
        assertEquals(1, CollectionHelper.getFirst(notEmptyCollection));
    }

    @Test
    void toCollectionTest() {
        assertDoesNotThrow(() -> CollectionHelper.toCollection("test"));
        assertDoesNotThrow(() -> CollectionHelper.toCollection(new Integer[]{1,2,3}));
        assertDoesNotThrow(() -> CollectionHelper.toCollection(new HashSet<>()));
    }

}
