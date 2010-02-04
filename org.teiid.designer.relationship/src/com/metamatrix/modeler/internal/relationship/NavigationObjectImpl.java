/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import org.eclipse.emf.common.util.URI;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.relationship.NavigationObject;

/**
 * NavigationObjectImpl
 */
public abstract class NavigationObjectImpl implements NavigationObject {

    private final URI modelObjectUri;
    protected final String label;

    /**
     * Construct an instance of NavigationObjectImpl.
     * 
     */
    protected NavigationObjectImpl(final URI modelObjectUri, final String label) {
        ArgCheck.isNotNull(label);
        this.modelObjectUri = modelObjectUri;
        this.label = label;
    }

    /**
     * @see com.metamatrix.modeler.relationship.NavigationLink#getModelObjectUri()
     */
    public URI getModelObjectUri() {
        return this.modelObjectUri;
    }
    
    /**
     * @return
     */
    public String getLabel() {
        return label;
    }

}
