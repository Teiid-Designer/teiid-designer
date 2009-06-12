/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.util;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.impl.DiagramImpl;
import com.metamatrix.modeler.core.workspace.ModelResource;


/** 
 * @since 5.0
 */
public class DiagramProxy extends DiagramImpl {
    private ModelResource modelResource;
    
    /** 
     * 
     * @since 5.0
     */
    public DiagramProxy(EObject target, String type) {
        super();
        setTarget(target);
        setType(type);
    }
    
    /** 
     * 
     * @since 5.0
     */
    public DiagramProxy(EObject target, String type, ModelResource modelResource) {
        super();
        setTarget(target);
        setType(type);
        this.modelResource = modelResource;
    }

    
    /** 
     * @return Returns the modelResource.
     * @since 5.0
     */
    public ModelResource getModelResource() {
        return this.modelResource;
    }
}
