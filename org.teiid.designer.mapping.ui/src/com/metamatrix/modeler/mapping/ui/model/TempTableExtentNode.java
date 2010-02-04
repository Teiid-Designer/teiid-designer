/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.model;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.model.AbstractDiagramModelNode;

/**
 * TempTableExtentNode
 */
public class TempTableExtentNode extends AbstractDiagramModelNode {

    public TempTableExtentNode(Diagram diagramModelObject, EObject modelObject) {
        super(diagramModelObject, modelObject);
        setName("TT"); //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return "TempTableExtentNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
