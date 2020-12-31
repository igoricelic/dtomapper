package org.indigo.dtomapper.providers;

import org.indigo.dtomapper.helpers.specification.PropertyScanner;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.helpers.specification.TransformManager;
import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.indigo.dtomapper.metadata.enums.Direction;
import org.indigo.dtomapper.providers.specification.Mapper;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

abstract class AbstractMapper implements Mapper {

    final PropertyScanner propertyScanner;

    final ReflectionHelper reflectionHelper;

    final TransformManager transformManager;

    AbstractMapper(PropertyScanner propertyScanner, ReflectionHelper reflectionHelper, TransformManager transformManager) {
        this.propertyScanner = propertyScanner;
        this.reflectionHelper = reflectionHelper;
        this.transformManager = transformManager;
    }

    Object readFromSource(Object sourceObject, PropertyMetadata metadata) {
        if(metadata.isRoot()) return sourceObject;

        Object result = sourceObject;
        for(String field: metadata.getPath()) {
            // find the access point to fetch the value
            Method getter = reflectionHelper.readGetterByField(field.toLowerCase(), result.getClass());
            if(Objects.isNull(getter)) return null;
            // fetch value
            result = reflectionHelper.invokeMethod(getter, result);
            if(Objects.isNull(result)) return null;
        }

        return result;
    }

    boolean checkIgnore(Direction ignorableDirection, Direction mappingDirection) {
        return mappingDirection.equals(ignorableDirection) || Direction.Bidirectional.equals(ignorableDirection);
    }

    <T> void writeToTarget(T target, Object value, PropertyMetadata metadata) {
        reflectionHelper.invokeMethod(metadata.getSetter(), target, value);
    }

    @Override
    public <T, E> void registerFunction(String functionName, Function<T, E> function) {
        transformManager.addTransformation(functionName, function);
    }

}
