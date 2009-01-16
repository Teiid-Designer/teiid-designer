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
