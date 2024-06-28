package org.indigo.dtomapper.mapping.model.customMapper;

import org.indigo.dtomapper.mapping.model.from.A;
import org.indigo.dtomapper.mapping.model.to.B;
import org.indigo.dtomapper.providers.specification.CustomMapper;
import org.indigo.dtomapper.providers.specification.Mapper;

public class NestedCustomMapper implements CustomMapper<A, B> {
    @Override
    public B mapLeftToRight(A left, Mapper mapper) {
        B b = new B();
        b.setFirstName(left.getName());
        b.setLastName(left.getSurname());
        return b;
    }

    @Override
    public A mapRightToLeft(B right, Mapper mapper) {
        A a = new A();
        a.setName(right.getFirstName());
        a.setSurname(right.getLastName());
        return a;
    }

}
