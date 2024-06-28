package org.indigo.dtomapper.providers.specification;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Mapper {

    default <T> T map(Object source, Class<T> targetClass) {
        return map(source, 0, targetClass);
    }

    <T> T map(Object source, int maxDepth, Class<T> targetClass);

    default <T> List<T> mapAsList(List<Object> sources, Class<T> targetClass) {
        return mapAsList(sources, 0, targetClass);
    }

    default <T> List<T> mapAsList(List<Object> sources, int maxDepth, Class<T> targetClass) {
        return sources.stream()
                .map(source -> map(source, maxDepth, targetClass))
                .collect(Collectors.toList());
    }

    /*
     * @Deprecated since 2.0.0: use registerCustomMapper instead
     */
    @Deprecated
    <T, E> void registerFunction(String functionName, Function<T, E> function);

    <L, R> void registerCustomMapper(CustomMapper<R, L> customMapper);

}
