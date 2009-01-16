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

package com.metamatrix.modeler.internal.relationship;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.relationship.NavigationNode;

/**
 * NavigationNodeImpl is an implentation of {@link NavigationNode}
 */
public class NavigationNodeImpl extends NavigationObjectImpl implements NavigationNode {

    private final int hc;
    private final EClass metaclass;
    private final String pathInModel;
    private final String toolTip;

    /**
     * Construct an instance of NavigationNodeImpl.
     * 
     */
    public NavigationNodeImpl(final EObject modelObject, final String label, 
                              final String pathInModel ) {
        super(EcoreUtil.getURI(modelObject),label);
        ArgCheck.isNotNull(modelObject);
        ArgCheck.isNotNull(EcoreUtil.getURI(modelObject));
        ArgCheck.isNotNull(pathInModel); 
        this.metaclass = modelObject.eClass(); 
        this.pathInModel = pathInModel;
        this.toolTip=""; //$NON-NLS-1$
        this.hc = super.getModelObjectUri().hashCode();   
    }
    
    /**
     * Construct an instance of NavigationNodeImpl.
     * 
     */
    public NavigationNodeImpl(final URI modelObjectUri, final String label, 
                              final EClass metaclass, final String pathInModel ) {
        super(modelObjectUri,label);
        ArgCheck.isNotNull(modelObjectUri);
        ArgCheck.isNotNull(metaclass); 
        ArgCheck.isNotNull(pathInModel); 
        this.metaclass = metaclass; 
        this.pathInModel = pathInModel;
        this.toolTip=""; //$NON-NLS-1$
        this.hc = modelObjectUri.hashCode();   
    }
    
    /**
     * Construct an instance of NavigationNodeImpl.
     * 
     */
    public NavigationNodeImpl(final URI modelObjectUri, final String label, 
                              final EClass metaclass, final String pathInModel,
                              final String toolTip ) {
        super(modelObjectUri,label);
        ArgCheck.isNotNull(modelObjectUri);
        ArgCheck.isNotNull(metaclass); 
        ArgCheck.isNotNull(pathInModel); 
        this.metaclass = metaclass; 
        this.pathInModel = pathInModel;
        this.toolTip = toolTip;
        this.hc = modelObjectUri.hashCode();   
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.hc;
    }
    
    /**
     * @return
     */
    public EClass getMetaclass() {
        return metaclass;
    }
    
    /**
     * Return the tooltip for this node.
     * @return tooltip for this node.
     */
    public String getToolTip() {
         return toolTip;
    }

    /**
     * Return the path of this node relative to this node's model.
     * @return the path in this model.
     */
    public String getPathInModel() {
        return pathInModel;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        //if ( this.getClass().isInstance(obj) ) {
        if (obj instanceof NavigationNodeImpl) {
            final NavigationNodeImpl that = (NavigationNodeImpl)obj;
            if ( that.hashCode() != this.hashCode() ) {
                return false;
            }
            final URI thatUri = that.getModelObjectUri();
            final URI thisUri = this.getModelObjectUri();
            return thisUri.equals(thatUri);
        }

        // Otherwise not comparable ...
        return false;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getModelObjectUri().toString() + '[' + this.metaclass.getName() + ']';
    }	
}
