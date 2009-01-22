/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.model;

import org.eclipse.draw2d.geometry.Dimension;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationContainerModelNode extends AbstractNavigationModelNode {

	/**
	 * Constructor
	 */
	public NavigationContainerModelNode(
		NavigationDiagramNode parentDiagramNode,
		Dimension initialSize,
		String label,
		String toolTip) {
		super(null, parentDiagramNode, label, toolTip);
		this.width = initialSize.width;
		this.height = initialSize.height;
	}


}
