package org.indigo.dtomapper.metadata;

import org.apache.commons.lang3.tuple.Pair;
import org.indigo.dtomapper.providers.specification.CustomMapper;

public class CustomMapperMetadata<L, R> {

    private final Class<L> leftType;

    private final Class<R> rightType;

    private final CustomMapper<L, R> customMapper;

    public CustomMapperMetadata(Pair<Class<L>, Class<R>> typesPair, CustomMapper<L, R> customMapper) {
        this.leftType = typesPair.getLeft();
        this.rightType = typesPair.getRight();
        this.customMapper = customMapper;
    }

    public Class<L> getLeftType() {
        return leftType;
    }

    public Class<R> getRightType() {
        return rightType;
    }

    public CustomMapper<L, R> getCustomMapper() {
        return customMapper;
    }

    public boolean support(Class<?> firstName, Class<?> secondType) {
        return (leftType.equals(firstName) && rightType.equals(secondType)) ||
                (leftType.equals(secondType) && rightType.equals(firstName));
    }

}
