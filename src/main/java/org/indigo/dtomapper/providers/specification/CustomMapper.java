package org.indigo.dtomapper.providers.specification;

public interface CustomMapper<L, R> {

    R mapLeftToRight(L left, Mapper mapper);

    L mapRightToLeft(R right, Mapper mapper);

}
