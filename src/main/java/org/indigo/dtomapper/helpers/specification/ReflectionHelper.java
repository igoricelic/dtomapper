package org.indigo.dtomapper.helpers.specification;

import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.exceptions.NoAccessPointException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public interface ReflectionHelper {

    /**
     * Checks that present class is get access point.
     */
    boolean isGetter(Method method);

    /**
     * Checks that present class is set access point.
     */
    boolean isSetter(Method method);

    /**
     * Checks that present class is void class or type.
     */
    boolean isVoid(Class<?> clazz);

    /**
     * Checks that present class is boolean class or type.
     */
    boolean isBoolean(Class<?> clazz);

    /**
     * Checks that present field is static field.
     */
    boolean isStatic(Field field);

    /**
     * Checks that present field has final modifier level.
     */
    boolean isFinal(Field field);

    /**
     * Checks that present field is parametrized type.
     * for exam.
     * field.type: List<String> output: true
     * field.type: String output: false
     */
    boolean isParametrizedType(Field field);

    /**
     * Checks that present class is creatable. Class is creatable, from the angle of dto mapper,
     * if contains default constructor.
     */
    boolean isCreatable(Class<?> clazz);

    /**
     * Checks that present method is access point to present field name.
     * for exam.
     * fieldName: firstName, method.name: getFirstName output: true
     * fieldName: isActive, method.name: isActive output: true
     * fieldName: firstName, method.name: getLastName output: false
     */
    boolean isAccessPoint(String fieldName, Method method);

    /**
     * Checks that present classes are compatible.
     * Two classes is compatible if if they are the same or assignable.
     * Primitive and corresponding not primitive classes are also compatible.
     */
    boolean isCompatibles(Class<?> type1, Class<?> type2);

    /**
     * Checks that present classes are compatible as class - type comparing.
     * (for exam. Boolean.class and Boolean.Type (boolean.class) are compatible)
     */
    boolean isPrimNotPrimCompatibles(Class<?> type1, Class<?> type2);

    /**
     * Two classes is assignable if if they are the same or one is a subclass of another class.
     */
    boolean isAssignableTypes(Class<?> type1, Class<?> type2);

    /**
     * Checks that present class is from JAVA bootstrap libraries like as java.lang(Long, String...),
     * java.util(List, Set, ArrayList...), java.time(LocalDateTime...)
     *
     * In action incompatible mapping we need to check that class to mapping is a java
     * bootstrap class.
     *
     * @param  type (candidate)
     */
    boolean isBootstrapType(Class<?> type);

    /**
     * Returns all methods from the passed class and all its superclasses all the way to the class Object.
     * Applies a hit-miss policy and puts results in the cache by input class.
     */
    List<Method> readAllMethods(Class<?> clazz);

    /**
     * Returns all fields from the passed class and all its superclasses all the way to the class Object.
     * Applies a hit-miss policy and puts results in the cache by input class.
     */
    List<Field> readAllFields(Class<?> clazz);

    /**
     * Returns get access method from desired class by field name.
     * Applies a hit-miss policy and puts results in the cache by input combination (fieldName, class).
     * @throws NoAccessPointException (if method does't exists)
     */
    Method readGetterByField(String fieldName, Class<?> clazz);

    /**
     * Returns modifier level of present object.
     * @throws IllegalStateException (if not readable modifier level)
     */
    int readModifiers(AccessibleObject accessibleObject);

    /**
     * Returns nested type of present parametrized type.
     * for exam: field.type: List<String> output: String
     * @throws IllegalStateException (if field isn't parametrized)
     */
    Class<?> readParametrizedType(Field field);

    /**
     * Method will be checks modifier level of accessible object (access point method)
     * and make method as accessible.
     * In the practise, private or protected metod will be change to public.
     */
    void makeAffordable(AccessibleObject accessibleObject);

    /**
     * Returns new instance object of desired class if the class is creatable.
     * @throws NoAccessPointException (if class isn't creatable)
     */
    <T> T newInstance(Class<T> clazz);

    /**
     * Returns result of invocation desired method on target object with present parameters.
     * @throws IllegalStateException (if invocation fail)
     */
    Object invokeMethod(Method method, Object target, Object... args);

}
