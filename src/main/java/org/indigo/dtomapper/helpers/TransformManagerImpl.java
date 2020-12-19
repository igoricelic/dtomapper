package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.helpers.specification.TransformationProvider;
import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.indigo.dtomapper.metadata.enums.TransformRelationState;
import org.indigo.dtomapper.providers.specification.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class TransformManagerImpl extends AbstractTransformer {

    public TransformManagerImpl(ReflectionHelper reflectionHelper, TransformationProvider transformationProvider) {
        super(reflectionHelper, transformationProvider);
    }

    @Override
    public Object transform(Object source, int actualDepth, PropertyMetadata metadata, Mapper mapper) {
        // if present custom mapping data - do custom mapping
        if(Objects.nonNull(metadata.getCustomMappingMetadata())) {
            return doCustomMapping(source, metadata.getCustomMappingMetadata());
        }
        List<Object> mappedValues = new ArrayList<>();
        List<Object> elementsToMapping = toCollection(source);

        TransformRelationState relationState = readRelationState(elementsToMapping.get(0).getClass(), metadata.getBaseType());
        for (Object element : elementsToMapping) {
            switch (relationState){
                case COMPATIBLE:
                    mappedValues.add(doCast(element, element.getClass(), metadata.getBaseType()));
                    break;
                case INCOMPATIBLE:
                    int depth = (actualDepth > 0) ? actualDepth - 1 : 0;
                    Object mappedValue = mapper.map(element, depth, metadata.getBaseType());
                    if(Objects.nonNull(mappedValue)) mappedValues.add(mappedValue);
                    break;
                case ERROR:
                    throw new IllegalStateException(String.format("Mapping from type '%s' to type '%s' isn't possible without custom transformation!", element.getClass(), metadata.getBaseType()));
            }
        }
        return toDesiredPack(mappedValues, metadata);
    }

    @Override
    public <T, E> void addTransformation(String name, Function<T, E> function) {
        this.transformationCache.put(name, function);
    }

}
