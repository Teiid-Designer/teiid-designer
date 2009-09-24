/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.metamatrix.modeler.relationship.ui.navigation.figure.DummyFocusNodeFigure;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DummyFocusNodeEditPart extends FocusNodeEditPart {


	/**
	 * @param figureFactory
	 */
	public DummyFocusNodeEditPart(NavigationDiagramFigureFactory figureFactory) {
		super(figureFactory);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
    protected IFigure createFigure() {
        
		Point location = new Point(100, 100);
		DummyFocusNodeFigure nodeFigure = (DummyFocusNodeFigure)getFigureFactory().createFigure(getModel());
		nodeFigure.setLocation(location);
        
		return nodeFigure;
	}

}
