package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.exceptions.NoTransformPointException;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.helpers.specification.TransformManager;
import org.indigo.dtomapper.helpers.specification.TransformationProvider;
import org.indigo.dtomapper.metadata.CustomMappingMetadata;
import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.indigo.dtomapper.metadata.enums.TransformRelationState;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class AbstractTransformer implements TransformManager {

    private final ReflectionHelper reflectionHelper;
    final ConcurrentMap<String, Function> transformationCache;
    private final TransformationProvider transformationProvider;
    private final ConcurrentMap<String, Method> transformationMethodCache;

    AbstractTransformer(ReflectionHelper reflectionHelper, TransformationProvider transformationProvider) {
        this.reflectionHelper = reflectionHelper;
        this.transformationProvider = transformationProvider;
        this.transformationCache = new ConcurrentHashMap<>();
        this.transformationMethodCache = new ConcurrentHashMap<>();
    }

    Object doCustomMapping(Object source, CustomMappingMetadata mappingMetadata) {
        Function mapperFunction = transformationCache.get(mappingMetadata.getFunction());
        if(Objects.nonNull(mapperFunction))
            return mapperFunction.apply(source);
        Method mapperMethod = getTransformMethod(mappingMetadata.getFunction(), mappingMetadata.getClazz());
        return reflectionHelper.invokeMethod(mapperMethod,null, source);
    }

    Object doCast(Object object, Class<?> sourceType, Class<?> targetType) {
        try {
            Assert.checkNotNull(object, sourceType, targetType);
            // if types are compatible, just return input object
            if(reflectionHelper.isCompatibles(sourceType, targetType)) return object;
            // otherwise, take a reference to transformer and do transform
            return ((Function) transformationProvider.findTransformation(sourceType, targetType)).apply(object);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Error in invocation transformation: %s", e.getMessage()));
        }
    }

    boolean isCastable(Class<?> sourceType, Class<?> targetType) {
        return reflectionHelper.isCompatibles(sourceType, targetType) || Objects.nonNull(transformationProvider.findTransformation(sourceType, targetType));
    }

    Method getTransformMethod(String methodName, Class<?> clazz) {
        if(transformationMethodCache.containsKey(methodName)) return transformationMethodCache.get(methodName);
        List<Method> results = reflectionHelper.readAllMethods(clazz)
                .stream().filter(m -> m.getName().equals(methodName))
                .collect(Collectors.toList());
        if(results.isEmpty()) throw new NoTransformPointException(String.format("Method '%s' not found in class '%s'!", methodName, clazz));
        if(results.size() > 1) throw new IllegalStateException("Ambiguous mapping methods!");
        transformationMethodCache.put(methodName, results.get(0));
        return results.get(0);
    }

    List<Object> toCollection(Object source) {
        if(Collection.class.isAssignableFrom(source.getClass()))
            return new ArrayList<>((Collection<Object>) source);
        if(source.getClass().isArray())
            return Arrays.asList((Object[]) source);
        return Collections.singletonList(source);
    }

    Object toDesiredPack(List<Object> results, PropertyMetadata metadata) {
        if(metadata.isNested()) {
            if(metadata.isArray()) {
                int idx = 0;
                Object array = Array.newInstance(metadata.getBaseType(), results.size());
                for (Object element : results) Array.set(array, idx++, element);
                return array;
            } else {
                if(List.class.isAssignableFrom(metadata.getCollectionType())) return results;
                if(Set.class.isAssignableFrom(metadata.getCollectionType())) return new LinkedHashSet<>(results);
            }
        }
        return results.get(0);
    }

    TransformRelationState readRelationState(Class<?> sourceType, Class<?> targetType) {
        if(isCastable(sourceType, targetType))
            return TransformRelationState.COMPATIBLE;
        if(reflectionHelper.isCreatable(targetType) && !reflectionHelper.isBootstrapType(targetType))
            return TransformRelationState.INCOMPATIBLE;
        return TransformRelationState.ERROR;
    }

}
