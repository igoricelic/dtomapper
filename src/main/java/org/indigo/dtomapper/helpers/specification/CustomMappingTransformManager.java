package org.indigo.dtomapper.helpers.specification;

import org.indigo.dtomapper.metadata.CustomMapperMetadata;
import org.indigo.dtomapper.providers.specification.CustomMapper;
import org.indigo.dtomapper.providers.specification.Mapper;

import java.util.Optional;

public interface CustomMappingTransformManager {

    /**
     * Register custom mapper for mapping between source and target types.
     * @param customMapper - instance of custom mapper
     */
    <L, R> void registerCustomMapper(CustomMapper<L, R> customMapper);

    /**
     * Check if exists custom mapper for mapping between source and target types.
     * @param sourceType - type of source object
     * @param targetType - desired type
     */
    <L, R> Optional<CustomMapperMetadata<?, ?>> findCustomMapper(Class<L> sourceType, Class<R> targetType);

    /**
     * Perform custom mapping between source and target types.
     * @param source - object to map
     * @param cm - custom mapper metadata
     */
    <L, R> Object doCustomMapping(Object source, CustomMapperMetadata<L, R> cm);

    /**
     * Set instance of mapper for custom mapping propose.
     * @param mapper - instance of mapper
     */
    void setMapper(Mapper mapper);

}
