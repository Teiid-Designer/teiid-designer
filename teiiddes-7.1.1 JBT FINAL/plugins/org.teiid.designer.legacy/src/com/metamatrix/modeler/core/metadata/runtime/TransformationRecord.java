/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

import java.util.List;

/**
 * TransformationRecord
 */
public interface TransformationRecord extends MetadataRecord {
	
    interface Types {
        public static final String MAPPING            = "Mapping"; //$NON-NLS-1$
        public static final String SELECT             = "Select"; //$NON-NLS-1$
        public static final String INSERT             = "Insert"; //$NON-NLS-1$
        public static final String UPDATE             = "Update"; //$NON-NLS-1$
        public static final String DELETE             = "Delete"; //$NON-NLS-1$
        public static final String PROCEDURE          = "Procedure"; //$NON-NLS-1$
    }

    /**
     * Get the transformation definition, which is typically an XML document containing the
     * tree of query nodes.
     * @return the string containing the definition of the transformation.
     */
    String getTransformation();

    /**
     * Get any bindings to the transformation, these could be inputset bindings.
     * @return a list of binding names
     */    
    List getBindings();

    /**
     * Get any paths to the various schemas that the XML document depends on.
     * @return a list of schema path names
     */    
    List getSchemaPaths();    

    /**
     * Get an identifier for the object that is the result of the transformation.
     * The transformed object is either a table or a procedure, depending upon the 
     * {@link MetadataConstants#getSqlTransformationTypeName(short)}.
     * @return an identifier for the virtual object
     */    
    Object getTransformedObjectID();

    /**
     * Get the transformation type, get the type 
     * {@link com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect.Types}
     * @return the string containing the type of the transformation.
     */
    String getTransformationType();

    /**
     * Return the type of TRANSFORMATION it is. 
     * @return transformTyype
     *
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.SQL_TRANSFORMATION_TYPES
     */
    String getType();

}