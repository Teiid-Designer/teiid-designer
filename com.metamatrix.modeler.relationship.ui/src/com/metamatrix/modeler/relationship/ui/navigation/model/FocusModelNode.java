/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.model;

import com.metamatrix.modeler.relationship.NavigationNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FocusModelNode extends AbstractNavigationModelNode {

	public FocusModelNode(
		NavigationNode nNode,
		NavigationDiagramNode parentDiagramNode,
		String labelName,
		String toolTip) {
		super(nNode, parentDiagramNode, labelName, toolTip);
	}

	@Override
    public String toString() {
		return "FocusModelNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
