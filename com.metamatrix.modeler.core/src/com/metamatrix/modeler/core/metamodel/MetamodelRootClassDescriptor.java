/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel;

import com.metamatrix.modeler.core.ExtensionDescriptor;

/**
 * MetamodelRootClassDescriptor
 */
public interface MetamodelRootClassDescriptor extends ExtensionDescriptor {
    
    /**
     * Return the maximum number of occurrences that the Class defined by
     * this descriptor can be used to create root entities within a model.
     * @return maxOccurs.
     */
    int getMaxOccurs();

}
