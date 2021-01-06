# DtoMapper
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

The basic role of this library is to reduce the boilerplate code generated during mapping and at the same time perform a transformation of the domain object into dto and vice versa.

Installation
=========

#### Using Maven: 
```
<dependency>
  <groupId>com.github.igoricelic</groupId>
  <artifactId>dtomapper</artifactId>
  <version>1.0.0</version>
</dependency>
```
#### Using Gradle:
```
implementation 'com.github.igoricelic:dtomapper:1.0.0'
```
#### (Optional) Create spring bean:

```java
@Configuration
class AppConfig {
    @Bean
    public Mapper getMapper() {
        return MapperFactory.getMapper();
    }
}
```

Get Started
=========
Explore this simple guide and see how to use a mapper. Let's go!

Primarily, we need a Mapper instance, for creating new instance we are using MapperFactory:
```java
Mapper mapper = MapperFactory.getMapper();
```
# Mapping

We can define domain classes for our example:
```java
class Person {
    @Id
    private Lond id;
    private String firstName;
    private String lastName;
    @ManyToOne
    private Address address;
    // constructors, get and set methods...
}

class Address {
    @Id
    private Lond id;
    private String street;
    private Integer houseNumber;
    @ManyToOne
    private City city;
    @OneToMany
    private Set<Person> residents;
    // constructors, get and set methods...
}

class City {
    @Id
    private Long id;
    private String name;
    private String country;
    // constructors, get and set methods...
}
```
And corresponding Dto class:
```java
class PersonDto {
    private Long id;
    private String firstName;
    private String lastName;
    @Property(depth=1)
    private Address address;
    // constructor, get and set methods...
}

class AddressDto {
    private Long id;
    private String street;
    private Integer number;
    @Property(path="city.name")
    private String city;
    @Property(path="city.country")
    private String country;
    @Property(depth=1)
    private Set<PersonDto> residents;
    // constructor, get and set methods...
}
```
The mapping is very simple:
```java
// with property to 0 depth level (without address)
PersonDto personDto = mapper.map(personObject, PersonDto.class);
// with property to max depth level 1 (with address, but address without 'residents')
PersonDto personDto = mapper.map(personObject, 1, PersonDto.class);
//  with property to max depth level 2 (with address, address with 'residents', resident person without address)
PersonDto personDto = mapper.map(personObject, 2, PersonDto.class);
```

## map
#### source_object 
The object we want to map

#### depth_level 
Denotes the total number of "nested steps" relative to the source object, as well as an upper limit for property mapping. We will understand the meaning if we recall the previous example:

In the following example: 
###### mapper.map(personObject, PersonDto.class)
we got an instance of PersonDto without the value of the property "Address", why?

***depth_level*** in this case is 0 and method is completely equivalent to
###### mapper.map(personObject, 0, PersonDto.class) 

This means that we will perform the mapping by picking only the contents whose depth level is <= 0.
Such behavior can be used in combination with lazy loading by using depth level 0 during group reading, but when we want an individual object with all relations we will use a higher depth level.

#### target_class
The type of result instance, the only thing that matters is that it has a default constructor so that the mapper can use it.

## @Property

#### depth
Default value is 0

#### path
Indicates the path to the desired property in relation to the initial object (root).
Compared to the previous example, the country from which the person is would be mapped as follows:
```java
@Property(path="address.city.country")
String personCountry;
```
root property can mark as '#', which means that this example would be valid if we wrote it as
```java
@Property(path="#.address.city.country")
String personCountry;
// or
@Property(path="#address.city.country")
String personCountry;
```
This feature can use to map the entire root as a single property, for example:
```java
class PersonDto {
    private Long id;
    ...
    @Property(path="#")
    private Person rootObject;
}
```

## @CustomMapping

For more advanced property mapping we need to define custom functions, and declare them through @CustomMapper annotation.
Let's look again, if we want an address of a person as a single string field:

```java
class PersonDto {
    ...
    @Property(path="address")
    @CustomMapping(function="getFullAddress")
    private String fullAddress;
    ...
    // constructor, get and set methods...
}
```
Of course, it's necessary to define and register desired mapping function:
```java
mapper.registerFunction("getFullAddress", (Address a) -> String.fromat("%s %d", a.getStreet(), a.getNumber()));
```
Parameter "path" in @Property annotation will join fullAddress from the dto with the address from the domain.
@CustomMapping says that we want to apply the "getFullAddress" function to the Address and expect the mapping result to be String.

Also, we can register method reference or static method from other class.
```java
mapper.registerFunction("checkIsNull", Objects::isNull);
// or
@CustomMapping(clazz=Objects.class, function="isNull");
```


Roadmap
=========
To develop this solution faster, contributions are welcome...

##### v1.0.0 (stable)
- base mapping
- recursive mapping
- nested mapping
- inheritance support: mapping property from superclass
- custom property mapping

Contributing
=========
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
Please make sure to update tests as appropriate.

