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

package com.metamatrix.modeler.diagram.ui.pakkage;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * IPackageDiagramManager
 */
public interface IPackageDiagramProvider {

    /**
     * This method allows a diagram type plugin to override the standard UML package diagram
     * construction based on the UmlAspect for UmlPackage.
     * @return
     */
    Diagram getPackageDiagram(ModelResource modelResource, EObject eObject, boolean forceCreate);
    
    /**
     * This method will return the nearest package diagram for an arbitrary eObject
     * @return
     */
     Diagram getPackageDiagram(Object targetObject, boolean forceCreate);

}
