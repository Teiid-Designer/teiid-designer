/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

/**
 * AbstractRelationshipSearchRecord
 */
public abstract class AbstractRelationshipRecord extends AbstractSearchRecord {

    private String name;
    private String objectUri;

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord#getUri()
     */
    public String getUri() {
        return objectUri;
    }

    /**
     * @param string
     */
    public void setUri( String string ) {
        objectUri = string;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.relationship.search.index.RelatedObjectRecord#getObjectName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param string
     */
    public void setName( String string ) {
        name = string;
    }

}
