package org.indigo.dtomapper.providers;

import org.indigo.dtomapper.helpers.PropertyScannerImpl;
import org.indigo.dtomapper.helpers.ReflectionHelperImpl;
import org.indigo.dtomapper.helpers.TransformManagerImpl;
import org.indigo.dtomapper.helpers.TransformationProviderImpl;
import org.indigo.dtomapper.helpers.specification.PropertyScanner;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.helpers.specification.TransformManager;
import org.indigo.dtomapper.providers.specification.Mapper;

public class MapperFactory {

    public static Mapper getMapper() {
        ReflectionHelper reflectionHelper = new ReflectionHelperImpl();
        PropertyScanner propertyScanner = new PropertyScannerImpl(reflectionHelper);
        TransformManager transformManager = new TransformManagerImpl(reflectionHelper, new TransformationProviderImpl());
        return new MapperImpl(propertyScanner, reflectionHelper, transformManager);
    }

}
