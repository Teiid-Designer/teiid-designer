/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.util.ModelContents;

/**
 * MTK implementation of a Resource
 */
public interface EmfResource extends MMXmiResource {
       
    /**
     * Returns the {@link Container} instance associated with
     * this resource set.
     * @return Container
     */
    Container getContainer();
    
    /**
     * Obtain the helper for the model contents.
     * @return the content helper; may be null if this resource is not loaded
     */
    ModelContents getModelContents();

 
}
