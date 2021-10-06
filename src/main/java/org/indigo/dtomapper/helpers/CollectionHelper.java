package org.indigo.dtomapper.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class CollectionHelper {

    public static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    public static boolean isNonEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static Collection<?> toCollection(Object object) {
        if(Collection.class.isAssignableFrom(object.getClass()))
            return new ArrayList<>((Collection<?>) object);
        if(object.getClass().isArray())
            return Arrays.asList((Object[]) object);
        return Collections.singletonList(object);
    }

    public static Object getFirst(Collection<?> collection) {
        return isEmpty(collection) ? null : collection.iterator().next();
    }

}
