/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.dummy;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * DummyDiagramPartFactory
 */
public class DummyDiagramPartFactory implements EditPartFactory {
    
    public EditPart createEditPart(EditPart iContext, Object iModel) {
        EditPart editPart = null;

        if( iModel instanceof DummyDiagramNode )
            editPart = new DummyDiagramEditPart();

        return editPart;
    }
}
