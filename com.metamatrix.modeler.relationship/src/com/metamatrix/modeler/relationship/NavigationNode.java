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
import org.eclipse.emf.ecore.EClass;

/**
 * A NavigationNode is a placeholder for a model object that is a participant in one
 * or more "association" or "relationship" instances.  When two model objects participate in 
 * in opposite sides of a relationship, a NavigationLink is used to represent the participation.
 * @see NavigationLink
 */
public interface NavigationNode extends NavigationObject {
    
    /**
     * Return the label for this node.  This is typically the same string returned
     * by the corresponding ItemProvider for this object.
     * @return the label for the node; may be null if the link doesn't have a label
     */
    public String getLabel();

    /**
     * Returns the URI to the {@link EObject model object} that this node represents.
     * @return the URI to the model object; never null
     */
    public URI getModelObjectUri();

    /**
     * Return the {@link EClass metaclass} for the model object.
     * @return the metaclass; never null
     */
    public EClass getMetaclass();

    /**
     * Obtain the model object's path in it's model.
     * @return the path to the object relative to it's model; never null
     */
    public String getPathInModel();
    
}
