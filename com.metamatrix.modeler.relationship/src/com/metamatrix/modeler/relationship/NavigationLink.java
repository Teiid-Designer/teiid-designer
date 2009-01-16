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

package com.metamatrix.modeler.relationship;

import org.eclipse.emf.common.util.URI;

/**
 * A NavigationLink represents the "association" or "relationship" between two {@link EObject model objects}.
 * However, "association" and "relationships" (in the general sense) often may relate a set of objects to a set
 * of objects, and therefore would be represented by multiple links.
 */
public interface NavigationLink extends NavigationObject {
    
    /**
     * Return the label for this link.
     * @return the label for the link; may be null if the link doesn't have a label
     */
    public String getLabel();

	/**
	 * Returns the URI to this link's {@link EObject model object}, if it has one.  A link may correspond to
     * exactly zero or one {@link EObject model object}, and will for generalized relationships
     * typically be reference an instance of {@link com.metamatrix.metamodels.relationship.Relationship}.
     * @return the URI to the model object, or null if this link does not correspond to any real model object.
	 */
	public URI getModelObjectUri();

    /**
     * Return the type for the link.
     * @return the type for the link; may be null if there is no type
     */
    public String getType();
}
