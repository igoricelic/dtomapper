package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.exceptions.IllegalStateException;

import java.util.Objects;

public class Assert {

    private static final String DEFAULT_NOT_NULL_MESSAGE = "Argument can't be null!";

    public static void checkNotNull(Object object) {
        checkNotNull(object, DEFAULT_NOT_NULL_MESSAGE);
    }

    public static void checkNotNull(Object... objects) {
        for (Object object : objects) checkNotNull(object, DEFAULT_NOT_NULL_MESSAGE);
    }

    public static void checkNotNull(Object object, String message) {
        if(Objects.isNull(object)) throw new IllegalStateException(message);
    }

}
