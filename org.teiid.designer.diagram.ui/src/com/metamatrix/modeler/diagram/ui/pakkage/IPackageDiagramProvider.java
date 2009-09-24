/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
