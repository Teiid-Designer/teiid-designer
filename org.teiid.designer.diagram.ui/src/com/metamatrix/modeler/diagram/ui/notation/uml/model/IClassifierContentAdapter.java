/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.model;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;

/**
 * IClassifierContentAdapter
 * Provides an interface between generic diagram world to specific content-type questions for
 * classifier objects.
 */
public interface IClassifierContentAdapter {

    /**
     * This method allows the generic UmlClassifierNode to find out from the particular
     * diagram type if inner classes should be shown in a classifier.
     * @param classifierEObject
     * @param diagram
     * @return
     */
    boolean showInnerClasses(EObject classifierEObject, Diagram diagram);
    
}
