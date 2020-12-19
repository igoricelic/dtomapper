package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssertTests {

    @Test
    void checkNotNullTest() {
        assertDoesNotThrow(() -> Assert.checkNotNull(5));
        assertThrows(IllegalStateException.class, () -> Assert.checkNotNull(null, "Parameter can't be null!"));
        assertThrows(IllegalStateException.class, () -> Assert.checkNotNull(5, 6, 7, null, 10));
    }

}
