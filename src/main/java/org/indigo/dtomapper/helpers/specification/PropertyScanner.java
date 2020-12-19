package org.indigo.dtomapper.helpers.specification;

import org.indigo.dtomapper.metadata.PropertyMetadata;

import java.util.List;

public interface PropertyScanner {

    /**
     * Returns list of metadata for each property in the class.
     * Metadata contains all relevant information about property as java type,
     * corresponding property from @Property annotation and mapping metadata.
     * @return metadata
     */
    List<PropertyMetadata> readMetadata(Class<?> clazz);

}
