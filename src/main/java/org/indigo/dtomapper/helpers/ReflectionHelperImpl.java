package org.indigo.dtomapper.helpers;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.indigo.dtomapper.exceptions.IllegalAccessException;
import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.exceptions.NoAccessPointException;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public final class ReflectionHelperImpl implements ReflectionHelper {

    private static final String GETTER_PREFIX = "get", GETTER_IS_PREFIX = "is", SETTER_PREFIX = "set";

    private final String[] BOOTSTRAP_PACKAGES = {"java.lang", "java.util", "java.time"};

    private final ConcurrentMap<Class<?>, List<Method>> methodCache;

    private final ConcurrentMap<ImmutablePair<String, Class<?>>, Method> getterCache;

    public ReflectionHelperImpl() {
        this.methodCache = new ConcurrentHashMap<>();
        this.getterCache = new ConcurrentHashMap<>();
    }

    public boolean isVoid(Class<?> clazz) {
        return void.class.equals(clazz) || Void.class.equals(clazz);
    }

    public boolean isBoolean(Class<?> clazz) {
        return boolean.class.equals(clazz) || Boolean.class.equals(clazz);
    }

    @Override
    public boolean isStatic(Field field) {
        Assert.checkNotNull(field);
        return Modifier.isStatic(field.getModifiers());
    }

    @Override
    public boolean isFinal(Field field) {
        Assert.checkNotNull(field);
        return Modifier.isFinal(field.getModifiers());
    }

    @Override
    public boolean isParametrizedType(Field field) {
        Assert.checkNotNull(field);
        return (field.getGenericType() instanceof ParameterizedType);
    }

    @Override
    public boolean isCreatable(Class<?> clazz) {
        try {
            Assert.checkNotNull(clazz);
            return !Objects.isNull(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public boolean isAccessPoint(String fieldName, Method method) {
        String methodName = method.getName().toLowerCase();
        // regular case
        // if(methodName.endsWith(fieldName.toLowerCase())) return true;
        String methodSuffix = methodName.length() > 3 ? methodName.substring(3) : "";
        if(methodSuffix.equals(fieldName.toLowerCase())) return true;
        /* edge case:
            primitive boolean field and method (getter or setter)
            for example: field - isActive getter - isActive setter - setActive
            field.getType().isPrimitive() && fieldName.startsWith("is")
        */
        if(fieldName.startsWith("is")) {
            return methodName.endsWith(fieldName.substring(2).toLowerCase());
        }
        return false;
    }

    @Override
    public boolean isCompatibles(Class<?> type1, Class<?> type2) {
        Assert.checkNotNull(type1);
        Assert.checkNotNull(type2);
        if(type1.equals(type2) || isAssignableTypes(type1, type2)) return true;
        int relation = Boolean.compare(type1.isPrimitive(), type2.isPrimitive());
        if(relation == 0) return false;
        return (relation > 0) ? isPrimNotPrimCompatibles(type2, type1) : isPrimNotPrimCompatibles(type1, type2);
    }

    @Override
    public boolean isPrimNotPrimCompatibles(Class<?> notPrimitiveClass, Class<?> primitiveClass) {
        try {
            Field typeField = notPrimitiveClass.getField("TYPE");
            return Optional.ofNullable((Class<?>) typeField.get(null))
                    .map(typeClass -> typeClass.equals(primitiveClass)).orElse(false);
        } catch (NoSuchFieldException | java.lang.IllegalAccessException e) {
            return false;
        }
    }

    @Override
    public boolean isAssignableTypes(Class<?> type1, Class<?> type2) {
        return (type1.isAssignableFrom(type2) || type2.isAssignableFrom(type1));
    }

    @Override
    public boolean isBootstrapType(Class<?> type) {
        return Stream.of(BOOTSTRAP_PACKAGES)
                .anyMatch(packagePrefix -> type.getName().startsWith(packagePrefix));
    }

    public boolean isGetter(Method method) {
        return (method.getParameterCount() == 0 && !isVoid(method.getReturnType()) && method.getName().startsWith(GETTER_PREFIX))
                ||
                (method.getName().startsWith(GETTER_IS_PREFIX) && isBoolean(method.getReturnType()));
    }

    public boolean isSetter(Method method) {
        return method.getName().startsWith(SETTER_PREFIX) && method.getParameterCount() == 1 && isVoid(method.getReturnType());
    }

    @Override
    public List<Method> readAllMethods(Class<?> clazz) {
        Assert.checkNotNull(clazz);
        if(methodCache.containsKey(clazz))
            return methodCache.get(clazz);
        List<Method> methods = new ArrayList<>();
        methodCache.putIfAbsent(clazz, methods);
        while (clazz != Object.class) {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    @Override
    public List<Field> readAllFields(Class<?> clazz) {
        Assert.checkNotNull(clazz);
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    @Override
    public Method readGetterByField(String fieldName, Class<?> clazz) {
        ImmutablePair<String, Class<?>> key = new ImmutablePair<>(fieldName, clazz);
        if(getterCache.containsKey(key)) return getterCache.get(key);
        Method getter = readAllMethods(clazz).stream()
                .filter(method -> isGetter(method) && isAccessPoint(fieldName, method))
                .findFirst()
                .orElse(null);
                // todo: razmisliti o sledecem
                // .orElseThrow(() -> new NoAccessPointException(String.format("Not found access point for property '%s' in class '%s'!", fieldName, clazz.getName())));
        if(Objects.nonNull(getter)) getterCache.put(key, getter);
        return getter;
    }

    public int readModifiers(AccessibleObject accessibleObject) {
        Assert.checkNotNull(accessibleObject);
        if (accessibleObject instanceof Field) {
            return ((Field) accessibleObject).getModifiers();
        } else if(accessibleObject instanceof Method) {
            return ((Method) accessibleObject).getModifiers();
        }
        throw new IllegalStateException("Modifiers of present object can't be read!");
    }

    @Override
    public Class<?> readParametrizedType(Field field) {
        if(!isParametrizedType(field))
            throw new IllegalStateException(String.format("Illegal state of mapping! Not present parametrized field '%s'", field.getName()));
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }

    public void makeAffordable(AccessibleObject accessibleObject) {
        Assert.checkNotNull(accessibleObject);
        if(Modifier.isFinal(readModifiers(accessibleObject))) {
            String throwableMessage = "Final %s element '%s'. Final elements can't be mapped!";
            if(accessibleObject instanceof Field)
                throwableMessage = String.format(throwableMessage, "field", ((Field) accessibleObject).getName());
            else if(accessibleObject instanceof Method)
                throwableMessage = String.format(throwableMessage, "method", ((Method) accessibleObject).getName());
            throw new IllegalAccessException(throwableMessage);
        }
        accessibleObject.setAccessible(true);
    }

    @Override
    public <T> T newInstance(Class<T> clazz) {
        try {
            if(!isCreatable(clazz))
                throw new NoAccessPointException(String.format("No default constructor for class '%s'!", clazz));
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | java.lang.IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new NoAccessPointException(String.format("No default constructor for class '%s'!", clazz));
        }
    }

    @Override
    public Object invokeMethod(Method method, Object target, Object... args) {
        Assert.checkNotNull(method);
        try {
            return method.invoke(target, args);
        } catch (java.lang.IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new IllegalStateException(String.format("Error in invocation method %s: %s", method.getName(), e.getMessage()));
        }
    }

}
