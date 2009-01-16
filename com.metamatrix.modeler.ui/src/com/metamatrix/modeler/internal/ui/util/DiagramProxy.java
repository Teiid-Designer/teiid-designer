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
