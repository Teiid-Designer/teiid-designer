/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.dummy;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
//import org.eclipse.draw2d.Label;
//import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;


/**
 * DummyDiagramEditPart
 */
public class DummyDiagramEditPart extends AbstractGraphicalEditPart {

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {

        Figure newFigure = new FreeformLayer();
        newFigure.setLayoutManager(new FreeformLayout());
        // Don't know why, but if you don't setOpaque(true), you cannot move by drag&drop!
        newFigure.setOpaque(true);
        newFigure.setBackgroundColor(ColorConstants.lightGray);
//        Label emptyLabel = new Label("EMPTY_DIAGRAM"); //$NON-NLS-1$
//        newFigure.add(emptyLabel);
//        emptyLabel.setLocation(new Point(20, 30));

        return newFigure;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {

    }
    
    /* (non-JavaDoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     * You must implement this method if you want you root model to have 
     * children!
    **/
    @Override
    protected List getModelChildren() {
        return Collections.EMPTY_LIST;
    }

}
