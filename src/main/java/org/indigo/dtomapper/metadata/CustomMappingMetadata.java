package org.indigo.dtomapper.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class CustomMappingMetadata {

    private final String function;

    private final Class<?> clazz;

}
