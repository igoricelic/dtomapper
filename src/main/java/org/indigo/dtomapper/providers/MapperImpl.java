package org.indigo.dtomapper.providers;

import org.indigo.dtomapper.helpers.Assert;
import org.indigo.dtomapper.helpers.specification.PropertyScanner;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.helpers.specification.TransformManager;
import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.indigo.dtomapper.metadata.enums.Direction;

import java.util.List;
import java.util.Objects;

final class MapperImpl extends AbstractMapper {

    MapperImpl(PropertyScanner propertyScanner, ReflectionHelper reflectionHelper, TransformManager transformManager) {
        super(propertyScanner, reflectionHelper, transformManager);
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass) {
        return map(source,0, targetClass);
    }

    @Override
    public <T> T map(Object source, int maxDepth, Class<T> targetClass) {
        Assert.checkNotNull(source, targetClass);
        return transform(source, maxDepth, targetClass, source.getClass());
    }

    private <T> T transform(Object sourceObject, int depth, Class<T> targetClass, Class<?> sourceClass) {
        if(depth < 0) return null;

        T t = reflectionHelper.newInstance(targetClass);
        List<PropertyMetadata> targetProperties = propertyScanner.readMetadata(targetClass);

        for (PropertyMetadata targetProperty: targetProperties) {
            if(targetProperty.getDepth() > depth || checkIgnore(targetProperty.getIgnoreDirection(), Direction.Incoming)) continue;

            // step 1: read value from source
            Object sourceValue = readFromSource(sourceObject, targetProperty);
            if(Objects.isNull(sourceValue)) continue;

            // step 2: transform source value to expected type
            Object mappedValue = transformManager.transform(sourceValue, depth, targetProperty,this);
            if(Objects.isNull(mappedValue)) continue;

            // step 3: write mapped value to target instance
            writeToTarget(t, mappedValue, targetProperty);
        }

        return t;
    }

}
