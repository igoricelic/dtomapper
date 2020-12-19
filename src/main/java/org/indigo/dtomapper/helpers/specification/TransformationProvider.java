package org.indigo.dtomapper.helpers.specification;

import java.util.function.Function;

public interface TransformationProvider {

    <T, E> Function<T, E> findTransformation(Class<T> clazzT, Class<E> clazzE);

}
