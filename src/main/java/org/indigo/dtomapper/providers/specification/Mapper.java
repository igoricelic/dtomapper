package org.indigo.dtomapper.providers.specification;

import java.util.function.Function;

public interface Mapper {

    <T> T map(Object source, Class<T> targetClass);

    <T> T map(Object source, int maxDepth, Class<T> targetClass);

    <T, E> void registerFunction(String functionName, Function<T, E> function);

}
