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

public final class PropertyScannerImpl extends AbstractPropertyEvaluator implements PropertyScanner {

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
            // default path is path to the field with equals name in the source class
            metadata.setPath(List.of(targetField.getName()));

            Property propertyAnnotation = targetField.getDeclaredAnnotation(Property.class);
            if(Objects.nonNull(propertyAnnotation)) {
                // if property annotation is present, default values will be override
                // set new depth level (default depth level is 0)
                metadata.setDepth(propertyAnnotation.depth());
                // if the path is presented, we will verify and parse it... otherwise, keep the default
                List<String> path = "".equals(propertyAnnotation.path()) ? metadata.getPath() : parse(propertyAnnotation.path());
                metadata.setPath(path);
                // checks that is present property is a root property (check that value is '#')
                metadata.setRoot(isRootProperty(path));
            }

            CustomMapping customMappingAnnotation = targetField.getDeclaredAnnotation(CustomMapping.class);
            if(Objects.nonNull(customMappingAnnotation)) {
                // if property annotation is present, read and set metadata for custom mapping (such as class and name of custom function)
                metadata.setCustomMappingMetadata(new CustomMappingMetadata(customMappingAnnotation.function(), customMappingAnnotation.clazz()));
            }

            Ignore ignoreAnnotation = targetField.getDeclaredAnnotation(Ignore.class);
            if(Objects.nonNull(ignoreAnnotation)) {
                // if property annotation is present, read and set metadata for ignoring
                metadata.setIgnoreDirection(ignoreAnnotation.direction());
            }

            // read and set base field type (java type of field)
            Class<?> javaType = targetField.getType();
            metadata.setBaseType(javaType);
            if(javaType.isArray()) {
                // if field is array, mark as array field and override base type with raw type (component type)
                metadata.setArray(true);
                metadata.setBaseType(javaType.getComponentType());
            } else if(reflectionHelper.isParametrizedType(targetField)) {
                // if field is collection, mark as collection field and override base type with raw type (nested type) and set collection type (such as List or Set...)
                metadata.setCollection(true);
                metadata.setCollectionType(javaType);
                metadata.setBaseType(reflectionHelper.readParametrizedType(targetField));
            }
            // field is nested if it's array or collection
            metadata.setNested(metadata.isArray() || metadata.isCollection());
            metadata.setEnum(metadata.getBaseType().isEnum());

            if(!isIgnorableDirection(metadata.getIgnoreDirection())) {
                // if field isn't ignorable in the 'Outgoing' direction, read and set access point as setter method
                Method accessPoint = setters.stream()
                        .filter(setter -> reflectionHelper.isAccessPoint(targetField.getName().toLowerCase(), setter))
                        .findFirst().orElseThrow(() -> new NoAccessPointException("Setter not found!"));
                // change visibility from access point method, for example if method is private or protected - make it a public
                reflectionHelper.makeAffordable(accessPoint);
                metadata.setSetter(accessPoint);
            }

            properties.add(metadata);
        }

        return properties;
    }



}
