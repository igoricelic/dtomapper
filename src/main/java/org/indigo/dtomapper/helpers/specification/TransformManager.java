package org.indigo.dtomapper.helpers.specification;

import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.indigo.dtomapper.providers.specification.Mapper;

import java.util.function.Function;

public interface TransformManager {

    Object transform(Object source, int actualDepth, PropertyMetadata metadata, Mapper mapper);

    <T, E> void addTransformation(String name, Function<T, E> function);

}
