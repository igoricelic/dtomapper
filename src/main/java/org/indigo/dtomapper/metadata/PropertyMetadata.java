package org.indigo.dtomapper.metadata;

import lombok.Data;
import org.indigo.dtomapper.metadata.enums.Direction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@Data
public final class PropertyMetadata {

    private List<String> path;

    private int depth = 0;

    private Field reference;

    private Method setter;

    private Class<?> baseType, collectionType;

    private boolean isRoot;

    private boolean isEnum;

    private boolean isNested, isCollection, isArray;

    private Direction ignoreDirection = Direction.None;

    private CustomMappingMetadata customMappingMetadata;

}
