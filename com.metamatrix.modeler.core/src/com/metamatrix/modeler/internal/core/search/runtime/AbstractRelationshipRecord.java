/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

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
