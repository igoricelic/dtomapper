package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.annotations.CustomMapping;
import org.indigo.dtomapper.annotations.Ignore;
import org.indigo.dtomapper.annotations.Property;
import org.indigo.dtomapper.exceptions.NoAccessPointException;
import org.indigo.dtomapper.helpers.specification.PropertyScanner;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.metadata.CustomMappingMetadata;
import org.indigo.dtomapper.metadata.PropertyMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class PropertyScannerImpl extends AbstractPropertyEvaluator implements PropertyScanner {

    private final ConcurrentMap<Class<?>, List<PropertyMetadata>> propertyCache;

    public PropertyScannerImpl(ReflectionHelper reflectionHelper) {
        super(reflectionHelper);
        this.propertyCache = new ConcurrentHashMap<>();
    }

    @Override
    public List<PropertyMetadata> readMetadata(Class<?> clazz) {
        if(propertyCache.containsKey(clazz))
            return propertyCache.get(clazz);
        List<PropertyMetadata> properties = new ArrayList<>();
        propertyCache.putIfAbsent(clazz, properties);

        List<Field> fields = reflectionHelper.readAllFields(clazz);
        List<Method> methods = reflectionHelper.readAllMethods(clazz);
        List<Method> setters = methods.stream().filter(reflectionHelper::isSetter).collect(Collectors.toList());

        for (Field targetField : fields) {
            if(reflectionHelper.isStatic(targetField) || reflectionHelper.isFinal(targetField)) continue;

            PropertyMetadata metadata = new PropertyMetadata();
            // default values
            metadata.setReference(targetField);
            metadata.setPath(List.of(targetField.getName()));

            Property propertyAnnotation = targetField.getDeclaredAnnotation(Property.class);
            if(Objects.nonNull(propertyAnnotation)) {
                metadata.setDepth(propertyAnnotation.depth());
                List<String> path = "".equals(propertyAnnotation.path()) ? metadata.getPath() : parse(propertyAnnotation.path());
                metadata.setPath(path);
                metadata.setRoot(isRootProperty(path));
            }

            CustomMapping customMappingAnnotation = targetField.getDeclaredAnnotation(CustomMapping.class);
            if(Objects.nonNull(customMappingAnnotation)) {
                metadata.setCustomMappingMetadata(new CustomMappingMetadata(customMappingAnnotation.function(), customMappingAnnotation.clazz()));
            }

            Ignore ignoreAnnotation = targetField.getDeclaredAnnotation(Ignore.class);
            if(Objects.nonNull(ignoreAnnotation)) {
                metadata.setIgnoreDirection(ignoreAnnotation.direction());
            }

            Class<?> javaType = targetField.getType();
            metadata.setBaseType(javaType);
            if(javaType.isArray()) {
                metadata.setArray(true);
                metadata.setBaseType(javaType.getComponentType());
            } else if(reflectionHelper.isParametrizedType(targetField)) {
                metadata.setCollection(true);
                metadata.setCollectionType(javaType);
                metadata.setBaseType(reflectionHelper.readParametrizedType(targetField));
            }
            metadata.setNested(metadata.isArray() || metadata.isCollection());
            metadata.setEnum(metadata.getBaseType().isEnum());

            Method accessPoint = setters.stream()
                    .filter(setter -> reflectionHelper.isAccessPoint(targetField.getName().toLowerCase(), setter))
                    .findFirst().orElseThrow(() -> new NoAccessPointException("Setter not found!"));
            reflectionHelper.makeAffordable(accessPoint);
            metadata.setSetter(accessPoint);

            properties.add(metadata);
        }

        return properties;
    }



}
