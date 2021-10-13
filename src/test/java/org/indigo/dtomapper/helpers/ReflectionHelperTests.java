package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.exceptions.IllegalAccessException;
import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.exceptions.NoAccessPointException;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.mapping.model.AbstractUtilSuperClass;
import org.indigo.dtomapper.mapping.model.ReflectionUtilClass;
import org.indigo.dtomapper.mapping.model.from.Person;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionHelperTests {

    private final ReflectionHelper reflectionHelper = new ReflectionHelperImpl();

    @Test
    void isGetterTest(){
        try {
            Method m1 = ReflectionUtilClass.class.getDeclaredMethod("getTextField");
            assertNotNull(m1);
            Method m2 = ReflectionUtilClass.class.getDeclaredMethod("isField");
            assertNotNull(m2);
            Method m3 = ReflectionUtilClass.class.getDeclaredMethod("setTextField", String.class);
            assertNotNull(m3);
            assertTrue(reflectionHelper.isGetter(m1));
            assertTrue(reflectionHelper.isGetter(m2));
            assertFalse(reflectionHelper.isGetter(m3));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    void isSetterTest(){
        try {
            Method m1 = ReflectionUtilClass.class.getDeclaredMethod("getTextField");
            assertNotNull(m1);
            Method m2 = ReflectionUtilClass.class.getDeclaredMethod("isField");
            assertNotNull(m2);
            Method m3 = ReflectionUtilClass.class.getDeclaredMethod("setTextField", String.class);
            assertNotNull(m3);
            assertFalse(reflectionHelper.isSetter(m1));
            assertFalse(reflectionHelper.isSetter(m2));
            assertTrue(reflectionHelper.isSetter(m3));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    void isVoidTest(){
        assertTrue(reflectionHelper.isVoid(void.class));
        assertFalse(reflectionHelper.isVoid(Long.class));
        assertTrue(reflectionHelper.isVoid(Void.class));
        assertFalse(reflectionHelper.isVoid(null));
    }

    @Test
    void isBooleanTest(){
        assertTrue(reflectionHelper.isBoolean(boolean.class));
        assertFalse(reflectionHelper.isBoolean(Long.class));
        assertTrue(reflectionHelper.isBoolean(Boolean.class));
        assertFalse(reflectionHelper.isBoolean(null));
    }

    @Test
    void isStaticTest(){
        try {
            Field staticField = ReflectionUtilClass.class.getDeclaredField("staticField");
            assertNotNull(staticField);
            Field notStaticField = ReflectionUtilClass.class.getDeclaredField("isField");
            assertNotNull(notStaticField);
            assertTrue(reflectionHelper.isStatic(staticField));
            assertFalse(reflectionHelper.isStatic(notStaticField));
        } catch (NoSuchFieldException e) {
            fail();
        }
    }

    @Test
    void isFinalTest(){
        try {
            Field finalField = ReflectionUtilClass.class.getDeclaredField("staticField");
            assertNotNull(finalField);
            Field notFinalField = ReflectionUtilClass.class.getDeclaredField("isField");
            assertNotNull(notFinalField);
            assertTrue(reflectionHelper.isFinal(finalField));
            assertFalse(reflectionHelper.isFinal(notFinalField));
        } catch (NoSuchFieldException e) {
            fail();
        }
    }

    @Test
    void isParametrizedTypeTest(){
        try {
            Field parametrizedField = ReflectionUtilClass.class.getDeclaredField("parameterizedField");
            assertNotNull(parametrizedField);
            Field notParametrizedField = ReflectionUtilClass.class.getDeclaredField("isField");
            assertNotNull(notParametrizedField);
            assertTrue(reflectionHelper.isParametrizedType(parametrizedField));
            assertFalse(reflectionHelper.isParametrizedType(notParametrizedField));
        } catch (NoSuchFieldException e) {
            fail();
        }
    }

    @Test
    void isCreatableTest(){
        assertFalse(reflectionHelper.isCreatable(Integer.class));
        assertTrue(reflectionHelper.isCreatable(ReflectionUtilClass.class));
    }

    @Test
    void isAccessPointTest(){
        try {
            Method method = ReflectionUtilClass.class.getDeclaredMethod("setField", boolean.class);
            assertTrue(reflectionHelper.isAccessPoint("isfield", method));
            assertFalse(reflectionHelper.isAccessPoint("textfield", method));
        } catch (NoSuchMethodException e) {
            fail();
        }
    }

    @Test
    void isCompatiblesTest(){
        assertTrue(reflectionHelper.isCompatibles(Boolean.class, boolean.class));
        assertTrue(reflectionHelper.isCompatibles(List.class, Collection.class));
        assertTrue(reflectionHelper.isCompatibles(Integer.class, Integer.class));
        assertTrue(reflectionHelper.isCompatibles(Integer.class, Number.class));
        assertFalse(reflectionHelper.isCompatibles(Integer.class, long.class));
        assertFalse(reflectionHelper.isCompatibles(Character.class, String.class));
        assertFalse(reflectionHelper.isCompatibles(List.class, Set.class));
        assertTrue(reflectionHelper.isCompatibles(short.class, short.class));
        assertFalse(reflectionHelper.isCompatibles(short.class, int.class));
    }

    @Test
    void isPrimNotPrimCompatiblesTest(){
        assertTrue(reflectionHelper.isCompatibles(short.class, Short.class));
        assertFalse(reflectionHelper.isCompatibles(short.class, Integer.class));
        assertTrue(reflectionHelper.isCompatibles(Long.class, long.class));
        assertFalse(reflectionHelper.isCompatibles(Number.class, int.class));
    }

    @Test
    void isAssignableTypesTest(){
        assertTrue(reflectionHelper.isAssignableTypes(List.class, ArrayList.class));
        assertFalse(reflectionHelper.isAssignableTypes(Set.class, List.class));
        assertTrue(reflectionHelper.isAssignableTypes(Iterable.class, List.class));
        assertFalse(reflectionHelper.isAssignableTypes(Character.class, Number.class));
    }

    @Test
    void isBootstrapTypeTest() {
        assertTrue(reflectionHelper.isBootstrapType(String.class));
        assertFalse(reflectionHelper.isBootstrapType(ReflectionUtilClass.class));
        assertTrue(reflectionHelper.isBootstrapType(Long.class));
        assertFalse(reflectionHelper.isBootstrapType(Person.class));
        assertTrue(reflectionHelper.isBootstrapType(Number.class));
        assertTrue(reflectionHelper.isBootstrapType(Object.class));
        assertTrue(reflectionHelper.isBootstrapType(ArrayList.class));
        assertTrue(reflectionHelper.isBootstrapType(Date.class));
        assertTrue(reflectionHelper.isBootstrapType(LocalDateTime.class));
    }

    @Test
    void readAllMethodsTest(){
        List<Method> results = reflectionHelper.readAllMethods(ReflectionUtilClass.class);
        assertNotNull(results);
        assertEquals(10, results.size());
    }

    @Test
    void readAllFieldsTest(){
        List<Field> results = reflectionHelper.readAllFields(ReflectionUtilClass.class);
        assertNotNull(results);
        assertEquals(8, results.size());
    }

    @Test
    void readGetterByFieldTest(){
        assertDoesNotThrow(() -> reflectionHelper.readGetterByField("textField", ReflectionUtilClass.class));
        // todo: razmisliti o sledecem
        // assertThrows(NoAccessPointException.class, () -> reflectionHelper.readGetterByField("parameterizedField", ReflectionUtilClass.class));
        assertNull(reflectionHelper.readGetterByField("parameterizedField", ReflectionUtilClass.class));
    }

    @Test
    void readModifiersTest(){
        assertDoesNotThrow(() -> reflectionHelper.readModifiers(ReflectionUtilClass.class.getDeclaredMethod("getIntField")));
        assertDoesNotThrow(() -> reflectionHelper.readModifiers(ReflectionUtilClass.class.getDeclaredField("intField")));
        assertThrows(IllegalStateException.class, () -> reflectionHelper.readModifiers(ReflectionUtilClass.class.getDeclaredConstructor()));
    }

    @Test
    void readParametrizedTypeTest(){
        try {
            assertThrows(IllegalStateException.class, () -> reflectionHelper.readParametrizedType(ReflectionUtilClass.class.getDeclaredField("intField")));
            assertEquals(String.class, reflectionHelper.readParametrizedType(ReflectionUtilClass.class.getDeclaredField("parameterizedField")));
        } catch (NoSuchFieldException e) {
            fail();
        }
    }

    @Test
    void makeAffordableTest(){
        assertThrows(IllegalAccessException.class, () -> reflectionHelper.makeAffordable(ReflectionUtilClass.class.getDeclaredField("staticField")));
        assertThrows(IllegalAccessException.class, () -> reflectionHelper.makeAffordable(AbstractUtilSuperClass.class.getDeclaredMethod("getT")));
        assertDoesNotThrow(() -> reflectionHelper.makeAffordable(ReflectionUtilClass.class.getDeclaredField("isField")));
        assertDoesNotThrow(() -> reflectionHelper.makeAffordable(ReflectionUtilClass.class.getDeclaredMethod("t")));

    }

    @Test
    void newInstanceTest(){
        assertDoesNotThrow(() -> reflectionHelper.newInstance(ReflectionUtilClass.class));
        assertThrows(NoAccessPointException.class, () -> reflectionHelper.newInstance(Long.class));
    }

    @Test
    void invokeMethodTest(){
        try {
            ReflectionUtilClass instance = reflectionHelper.newInstance(ReflectionUtilClass.class);
            assertNotNull(instance);
            Method setter = ReflectionUtilClass.class.getDeclaredMethod("setTextField", String.class);
            Method getter = ReflectionUtilClass.class.getDeclaredMethod("getTextField");
            assertDoesNotThrow(() -> reflectionHelper.invokeMethod(setter, instance, "hello"));
            assertEquals("hello", reflectionHelper.invokeMethod(getter, instance));
        } catch (NoSuchMethodException e) {
            fail();
        }
    }

}
