package org.indigo.dtomapper.metadata.enums;

public enum TransformRelationState {

    /*
     * It isn't possible to perform the transformation from the source to the target type
     */
    ERROR,

    /*
     * Object of the target type is obtained by casting or other simple transformations of the source object
     */
    COMPATIBLE,

    /*
     * An object of the target type can't be obtained by casting.
     * We will create an default instance of the target type and go for nested property mapping
     */
    INCOMPATIBLE

}
