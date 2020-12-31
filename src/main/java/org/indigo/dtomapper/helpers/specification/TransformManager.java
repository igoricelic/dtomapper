package org.indigo.dtomapper.helpers.specification;

import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.indigo.dtomapper.providers.specification.Mapper;

import java.util.function.Function;

public interface TransformManager {

    /*
     * Performs the transformation of the type of source object into the desired type defined by metadata.
     * Argument 'mapper' provide recursive logic if need a inner nested mapping.
     * Argument 'depth' define a max number of steps in the depth when mapping.
     * @param source
     * @param actualDepth
     * @param metadata
     * @param mapper
     * @return mapped result
     */
    Object transform(Object source, int actualDepth, PropertyMetadata metadata, Mapper mapper);

    /*
     * Api to registration custom transformation by name of transformation.
     * Transformation will use method refernece.
     * @param name
     * @param function
     */
    <T, E> void addTransformation(String name, Function<T, E> function);

}
