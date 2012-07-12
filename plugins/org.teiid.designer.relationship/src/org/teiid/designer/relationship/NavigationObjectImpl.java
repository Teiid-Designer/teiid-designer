/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship;

import org.eclipse.emf.common.util.URI;
import org.teiid.core.util.CoreArgCheck;


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
        CoreArgCheck.isNotNull(label);
        this.modelObjectUri = modelObjectUri;
        this.label = label;
    }

    /**
     * @see org.teiid.designer.relationship.NavigationLink#getModelObjectUri()
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
