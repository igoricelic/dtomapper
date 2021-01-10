package org.indigo.dtomapper.helpers.specification;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public interface ReflectionHelper {

    /*
     * Checks that present class is get access point.
     * @param method - candidate
     * @return - true if method is setter, otherwise false
     */
    boolean isGetter(Method method);

    /*
     * Checks that present class is set access point.
     * @param method - candidate
     * @return - true if method is setter, otherwise false
     */
    boolean isSetter(Method method);

    /*
     * Checks that present class is void class or type.
     * @param clazz - reference to field for check
     * @return - true if class is void
     */
    boolean isVoid(Class<?> clazz);

    /*
     * Checks that present class is boolean class or type.
     * @param clazz - reference to field for check
     * @return - true if class is compatible with boolean
     */
    boolean isBoolean(Class<?> clazz);

    /*
     * Checks that present field is static field.
     * @param field - reference to field for check
     * @return - true if field is static, otherwise false
     */
    boolean isStatic(Field field);

    /*
     * Checks that present field has final modifier level.
     * @param field - reference to field for check
     * @return - true if field is final, otherwise false
     */
    boolean isFinal(Field field);

    /*
     * Checks that present field is parametrized type.
     * @param field  - reference to field
     * @return - true if field is parametrized, otherwise false
     */
    boolean isParametrizedType(Field field);

    /*
     * Checks that present class is creatable. Class is creatable, from the angle of dto mapper,
     * if contains default constructor.
     * @param clazz - reference to class for check
     * @return - true if class has default constructor, otherwise false
     */
    boolean isCreatable(Class<?> clazz);

    /*
     * Checks that present method is access point to present field name.
     * fieldName: firstName, method.name: getFirstName output: true
     * fieldName: isActive, method.name: isActive output: true
     * fieldName: firstName, method.name: getLastName output: false
     * @param fieldName - the name of the field for which we are looking for an access point
     * @param method - candidate to access point
     * @return - true if method is access point for field, otherwise false
     */
    boolean isAccessPoint(String fieldName, Method method);

    /*
     * Checks that present classes are compatible.
     * Two classes is compatible if if they are the same or assignable.
     * Primitive and corresponding not primitive classes are also compatible.
     * @param type1 - reference to first type
     * @param type2 - reference to second type
     * @return - true if types are compatible (equals, castable or assignable), otherwise false
     */
    boolean isCompatibles(Class<?> type1, Class<?> type2);

    /*
     * Checks that present classes are compatible as class - type comparing.
     * (for exam. Boolean.class and Boolean.Type (boolean.class) are compatible)
     * @param notPrimitiveClass - type of not primitive candidate
     * @param primitiveClass - type of primitive candidate
     * @return - true if classes are compatible and we can use Autoboxing  automatic conversion, otherwise false
     */
    boolean isPrimNotPrimCompatibles(Class<?> notPrimitiveClass, Class<?> primitiveClass);

    /*
     * Checks that present classes are assignable.
     * Two classes is assignable if if they are the same or one is a subclass of another class.
     * @param type1 - reference to first type
     * @param type2 - reference to second type
     * @return - true if types are assignable, otherwise false
     */
    boolean isAssignableTypes(Class<?> type1, Class<?> type2);

    /*
     * Checks that present class is from JAVA bootstrap libraries like as java.lang(Long, String...),
     * java.util(List, Set, ArrayList...), java.time(LocalDateTime...)
     *
     * In action incompatible mapping we need to check that class to mapping is a java
     * bootstrap class.
     *
     * @param  type - candidate
     * @return - true if class is bootstrap, otherwise false
     */
    boolean isBootstrapType(Class<?> type);

    /*
     * Returns all methods from the passed class and all its superclasses all the way to the class Object.
     * Applies a hit-miss policy and puts results in the cache by input class.
     * @param clazz - reference to type
     * @return - a list of methods declared in a class or any superclass other than the Object
     */
    List<Method> readAllMethods(Class<?> clazz);

    /*
     * Returns all fields from the passed class and all its superclasses all the way to the class Object.
     * Applies a hit-miss policy and puts results in the cache by input class.
     * @param clazz - reference to type
     * @return - a list of fields declared in a class or any superclass other than the Object
     */
    List<Field> readAllFields(Class<?> clazz);

    /*
     * Returns get access method from desired class by field name.
     * Applies a hit-miss policy and puts results in the cache by input combination (fieldName, class).
     * @throws NoAccessPointException - if method does't exists
     */
    Method readGetterByField(String fieldName, Class<?> clazz);

    /*
     * Returns modifier level of present object.
     * @throws IllegalStateException - if not readable modifier level
     */
    int readModifiers(AccessibleObject accessibleObject);

    /*
     * Returns nested type of present parametrized type.
     * for exam: field.type: List String output: String
     * @param field - candidate
     * @return - nested type if type is parametrized, otherwise throw exception
     * @throws IllegalStateException - if field isn't parametrized
     */
    Class<?> readParametrizedType(Field field);

    /*
     * Method will be checks modifier level of accessible object (access point method)
     * and make method as accessible.
     * In the practise, private or protected metod will be change to public.
     * @param accessibleObject - field or method who we need to make affordable
     * @throws IllegalAccessException - if field or method is a final
     */
    void makeAffordable(AccessibleObject accessibleObject);

    /*
     * Returns new instance object of desired class if the class is creatable.
     * @param clazz - type from which we want to get an instance
     * @return - new instance of desired type
     * @throws NoAccessPointException - if class isn't creatable
     */
    <T> T newInstance(Class<T> clazz);

    /*
     * Returns result of invocation desired method on target object with present parameters.
     * @param method - reference to method who want to invoke
     * @param target - the object over which we want to invoke the method, if method is static, value of 'target' is null
     * @param args - arguments to method
     * @return - result of invocation
     * @throws IllegalStateException - if invocation fail
     */
    Object invokeMethod(Method method, Object target, Object... args);

}
