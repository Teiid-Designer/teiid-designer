/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.figure;

import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;


/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FocusNodeFigure extends AbstractNavigationNodeFigure {
	/**
	 * 
	 */
	public FocusNodeFigure(NavigationModelNode modelNode, Image image, String toolTip) {
		super(modelNode, image);
		setToolTip(toolTip);
		setBackgroundColor(UiConstants.Colors.FOCUS_NODE_BKGD);
	}
	
	@Override
    protected boolean useLocalCoordinates(){
		return true;
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#layoutFigure()
	 */
	@Override
    public void layoutFigure() {
		setSizeToMinimum();
	}

}
