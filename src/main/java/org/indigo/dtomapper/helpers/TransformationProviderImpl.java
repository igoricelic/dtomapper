package org.indigo.dtomapper.helpers;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.indigo.dtomapper.helpers.specification.TransformationProvider;

import java.time.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public final class TransformationProviderImpl implements TransformationProvider {

    private final ConcurrentMap<ImmutablePair<Class, Class>, Function> transformations;

    public TransformationProviderImpl() {
        transformations = new ConcurrentHashMap<>();
        initialize();
    }

    @Override
    public <T, E> Function<T, E> findTransformation(Class<T> clazzT, Class<E> clazzE) {
        return transformations.getOrDefault(new ImmutablePair<>(clazzT, clazzE), null);
    }

    private void initialize() {

        transformations.put(new ImmutablePair<>(Long.class, Integer.class), (Object l) -> ((Long) l).intValue());
        transformations.put(new ImmutablePair<>(Long.class, String.class), (Object l) -> ((Long) l).toString());
        transformations.put(new ImmutablePair<>(Long.class, int.class), (Object l) -> ((Long) l).intValue());

        transformations.put(new ImmutablePair<>(long.class, Integer.class), (Object l) -> ((Long) l).intValue());
        transformations.put(new ImmutablePair<>(long.class, String.class), (Object l) -> ((Long) l).toString());
        transformations.put(new ImmutablePair<>(long.class, int.class), (Object l) -> ((Long) l).intValue());

        transformations.put(new ImmutablePair<>(Integer.class, Long.class), (Object l) -> ((Integer) l).longValue());
        transformations.put(new ImmutablePair<>(Integer.class, String.class), (Object l) -> ((Integer) l).toString());
        transformations.put(new ImmutablePair<>(Integer.class, long.class), (Object l) -> ((Integer) l).longValue());

        transformations.put(new ImmutablePair<>(int.class, Long.class), (Object l) -> ((Integer) l).longValue());
        transformations.put(new ImmutablePair<>(int.class, String.class), (Object l) -> ((Integer) l).toString());
        transformations.put(new ImmutablePair<>(int.class, long.class), (Object l) -> ((Integer) l).longValue());

        // Date & Time transformations
        // transformations.put(new ImmutablePair<>(Date.class, LocalDate.class), (Object date) -> LocalDate.ofInstant(((Date) date).toInstant(), ZoneId.systemDefault()));
        transformations.put(new ImmutablePair<>(Date.class, LocalDate.class), (Object date) -> ((Date) date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        // transformations.put(new ImmutablePair<>(Date.class, LocalTime.class), (Object date) -> LocalTime.ofInstant(((Date) date).toInstant(), ZoneId.systemDefault()));
        transformations.put(new ImmutablePair<>(Date.class, LocalDate.class), (Object date) -> ((Date) date).toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
        transformations.put(new ImmutablePair<>(Date.class, LocalDateTime.class), (Object date) -> LocalDateTime.ofInstant(((Date) date).toInstant(), ZoneId.systemDefault()));

        transformations.put(new ImmutablePair<>(LocalDateTime.class, Date.class), (Object date) -> Date.from(((LocalDateTime) date).toInstant(ZoneOffset.UTC)));
        transformations.put(new ImmutablePair<>(LocalDateTime.class, LocalDate.class), (Object date) -> ((LocalDateTime) date).toLocalDate());
        transformations.put(new ImmutablePair<>(LocalDateTime.class, LocalTime.class), (Object date) -> ((LocalDateTime) date).toLocalTime());

    }

}
