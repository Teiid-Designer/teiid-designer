/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui;

import com.metamatrix.metamodels.diagram.Diagram;

/**
 * IDiagramProvider
 * This class provides an interface which can be generically called by the modeler to see if any if a selected
 * generic Diagram type can be deleted. It is intended to be implemented by the diagram content providers...
 */
public interface IDiagramProvider {

    /**
     * This method provides a simple way for each diagram type to control whether or not
     * a diagram can be deleted.  Package, Mapping, Transformation should return false, while
     * Custom diagrams should return true;
     * @return canDelete
     */
    boolean canDelete(Diagram diagram);

}
