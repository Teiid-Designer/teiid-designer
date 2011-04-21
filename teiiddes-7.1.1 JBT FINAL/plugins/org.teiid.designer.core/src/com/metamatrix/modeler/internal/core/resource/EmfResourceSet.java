/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import com.metamatrix.modeler.core.container.Container;

/**
 * MTK implementation of a ResourceSet
 */
public interface EmfResourceSet {
       
    /**
     * Returns the {@link Container} instance associated with
     * this resource set.
     * @return Container
     */
    Container getContainer();
}
