/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.figure;

import org.eclipse.draw2d.ColorConstants;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;


/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DummyFocusNodeFigure extends FocusNodeFigure {

	/**
	 */
	public DummyFocusNodeFigure(NavigationModelNode navNode) {
		super(navNode, null, null);
		setBackgroundColor(ColorConstants.blue);
		setSize(21, 21);
	}

}
