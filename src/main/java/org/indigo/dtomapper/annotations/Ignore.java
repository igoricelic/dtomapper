package org.indigo.dtomapper.annotations;

import org.indigo.dtomapper.metadata.enums.Direction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {

    Direction direction() default Direction.Bidirectional;

}
