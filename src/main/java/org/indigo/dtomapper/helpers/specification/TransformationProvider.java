package org.indigo.dtomapper.helpers.specification;

import java.util.function.Function;

public interface TransformationProvider {

    /*
     * Returns the transformation from source to desired type if exists. Default value is a null.
     * @param clazzT
     * @param clazzE
     * @return
     */
    <T, E> Function<T, E> findTransformation(Class<T> clazzT, Class<E> clazzE);

}
