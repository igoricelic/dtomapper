package org.indigo.dtomapper.helpers;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.exceptions.NoTransformPointException;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.helpers.specification.TransformManager;
import org.indigo.dtomapper.helpers.specification.TransformationProvider;
import org.indigo.dtomapper.mapping.model.customMapper.NestedCustomMapper;
import org.indigo.dtomapper.mapping.model.from.*;
import org.indigo.dtomapper.mapping.model.to.B;
import org.indigo.dtomapper.mapping.model.to.CountryDto;
import org.indigo.dtomapper.mapping.model.to.PersonDto;
import org.indigo.dtomapper.mapping.model.to.WorkerDto;
import org.indigo.dtomapper.metadata.CustomMapperMetadata;
import org.indigo.dtomapper.metadata.CustomMappingMetadata;
import org.indigo.dtomapper.metadata.PropertyMetadata;
import org.indigo.dtomapper.metadata.enums.TransformRelationState;
import org.indigo.dtomapper.providers.MapperFactory;
import org.indigo.dtomapper.providers.specification.CustomMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransformManagerTests {

    private TransformManager transformManager;

    private AbstractTransformer abstractTransformer;

    @BeforeAll
    void setup() {
        final ReflectionHelper reflectionHelper = new ReflectionHelperImpl();
        final TransformationProvider transformationProvider = new TransformationProviderImpl();
        transformManager = new TransformManagerImpl(reflectionHelper, transformationProvider);
        abstractTransformer = (AbstractTransformer) transformManager;
    }

    @Test
    void transformTest() {
        List<Integer> inputValue = Arrays.asList(1, 2, 3, 4, 5);
        PropertyMetadata metadata = new PropertyMetadata();
        metadata.setBaseType(Long.class);
        metadata.setNested(true);
        metadata.setArray(true);
        // compatible relation state
        assertTrue(transformManager.transform(inputValue,0, metadata,null) instanceof Long[]);
        // error relation state
        assertThrows(IllegalStateException.class, () -> transformManager.transform(LocalDateTime.now(),0, metadata,null));
        try {
            PropertyMetadata incompatibleProperty = new PropertyMetadata();
            incompatibleProperty.setBaseType(CountryDto.class);
            incompatibleProperty.setNested(false);
            Country country = new Country("Serbia", "rs");
            // incompatible relation state
            CountryDto countryDto = (CountryDto) transformManager.transform(country, 0, incompatibleProperty, MapperFactory.getMapper());
            assertEquals("Serbia", countryDto.getName());
            assertEquals("rs", countryDto.getDomain());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void addTransformationTest() {
        assertDoesNotThrow(() -> transformManager.addTransformation("isNull", Objects::isNull));
        assertDoesNotThrow(() -> transformManager.addTransformation("hashCode", Objects::hashCode));
    }

    @Test
    void doCustomMappingTest() {
        // register custom transformation function
        abstractTransformer.addTransformation("increment", (Integer a) -> a+1);

        // prepare custom mapping metadata for 'increment' function
        CustomMappingMetadata mappingMetadata1 = new CustomMappingMetadata("increment", Void.class);
        assertEquals(2, abstractTransformer.doCustomMapping(1, mappingMetadata1));

        // prepare custom mapping metadata for 'nonNull' method from Objects class
        CustomMappingMetadata mappingMetadata2 = new CustomMappingMetadata("nonNull", Objects.class);
        assertTrue((boolean) abstractTransformer.doCustomMapping("hello", mappingMetadata2));
        assertFalse((boolean) abstractTransformer.doCustomMapping(null, mappingMetadata2));
    }

    @Test
    void toCustomMappingTestNew() {
        // create custom mapper
        CustomMapperMetadata<A, B> metadata =
                new CustomMapperMetadata<>(new ImmutablePair<>(A.class, B.class), new NestedCustomMapper());

        // create source object
        A a = new A();
        a.setName("John");
        a.setSurname("Doe");

        // test transformations
        Object result = abstractTransformer.doCustomMapping(a, metadata);
        assertTrue(result instanceof B);
        B b = (B) result;
        assertEquals("John", b.getFirstName());
        assertEquals("Doe", b.getLastName());

        Object reverseResult = abstractTransformer.doCustomMapping(b, metadata);
        assertTrue(reverseResult instanceof A);
        A reverseA = (A) reverseResult;
        assertEquals("John", reverseA.getName());
        assertEquals("Doe", reverseA.getSurname());
    }

    @Test
    void doCastTest(){
        assertTrue(abstractTransformer.doCast(3L, Long.class, Integer.class) instanceof Integer);
        assertEquals("3", abstractTransformer.doCast(3L, Long.class, String.class));
        assertThrows(IllegalStateException.class, () -> abstractTransformer.doCast(LocalDateTime.now(), LocalDateTime.class, Long.class));
    }

    @Test
    void isCastableTest(){
        assertTrue(abstractTransformer.isCastable(Long.class, long.class));
        assertTrue(abstractTransformer.isCastable(long.class, String.class));
        assertTrue(abstractTransformer.isCastable(int.class, long.class));
        assertFalse(abstractTransformer.isCastable(Date.class, ArrayList.class));
        assertFalse(abstractTransformer.isCastable(Person.class, PersonDto.class));
    }

    @Test
    void getTransformMethodTest(){
        // isCompatible (0)
        assertThrows(NoTransformPointException.class, () -> abstractTransformer.getTransformMethod("isCompatible", Objects.class));
        // nonNull (1)
        assertDoesNotThrow(() -> abstractTransformer.getTransformMethod("nonNull", Objects.class));
        // requireNonNull (2+)
        assertThrows(IllegalStateException.class, () -> abstractTransformer.getTransformMethod("requireNonNull", Objects.class));
    }

    @Test
    void toDesiredPackTest(){
        List<Object> objectsToMapping = Collections.singletonList(1);

        PropertyMetadata propertyMetadata = new PropertyMetadata();
        propertyMetadata.setNested(false);
        assertEquals(1, abstractTransformer.toDesiredPack(objectsToMapping, propertyMetadata));

        propertyMetadata.setNested(true);
        propertyMetadata.setArray(true);
        propertyMetadata.setBaseType(Integer.class);
        assertTrue(abstractTransformer.toDesiredPack(objectsToMapping, propertyMetadata) instanceof Integer[]);

        propertyMetadata.setArray(false);
        propertyMetadata.setCollection(true);
        propertyMetadata.setCollectionType(Set.class);
        assertTrue(abstractTransformer.toDesiredPack(objectsToMapping, propertyMetadata) instanceof Set);

        propertyMetadata.setCollectionType(List.class);
        assertTrue(abstractTransformer.toDesiredPack(objectsToMapping, propertyMetadata) instanceof List);
    }

    @Test
    void readRelationStateTest(){
        assertEquals(TransformRelationState.ERROR, abstractTransformer.readRelationState(Long.class, Date.class));
        assertEquals(TransformRelationState.ERROR, abstractTransformer.readRelationState(Character.class, String.class));
        assertEquals(TransformRelationState.INCOMPATIBLE, abstractTransformer.readRelationState(Person.class, PersonDto.class));
        assertEquals(TransformRelationState.INCOMPATIBLE, abstractTransformer.readRelationState(Address.class, PersonDto.class));
        assertEquals(TransformRelationState.COMPATIBLE, abstractTransformer.readRelationState(Long.class, long.class));
        assertEquals(TransformRelationState.COMPATIBLE, abstractTransformer.readRelationState(Long.class, int.class));
    }

    @Test
    void findCustomMapperTest() {
        // register mapper
        abstractTransformer.registerCustomMapper(new NestedCustomMapper());

        // find custom mapper
        assertTrue(abstractTransformer.findCustomMapper(A.class, B.class).isPresent());
        assertTrue(abstractTransformer.findCustomMapper(B.class, A.class).isPresent());
        assertFalse(abstractTransformer.findCustomMapper(Worker.class, WorkerDto.class).isPresent());
    }

    @Test
    void registerCustomMapperTest() {
        assertThrows(IllegalStateException.class, () -> abstractTransformer.registerCustomMapper(null));
        CustomMapper<A, B> customMapper = new NestedCustomMapper();
        assertDoesNotThrow(() -> abstractTransformer.registerCustomMapper(customMapper));
    }

    @Test
    void setMapperTest() {
        assertThrows(IllegalStateException.class, () -> abstractTransformer.setMapper(null));
        assertDoesNotThrow(() -> abstractTransformer.setMapper(MapperFactory.getMapper()));
    }

}
