/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.model;

import org.eclipse.draw2d.geometry.Dimension;
import com.metamatrix.modeler.relationship.NavigationNode;


/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipModelNode extends AbstractNavigationModelNode {
	
	public RelationshipModelNode(NavigationNode nNode, NavigationDiagramNode parentDiagramNode, String labelName) {
		super(nNode, parentDiagramNode, labelName);
	}
	
	public RelationshipModelNode(NavigationDiagramNode parentDiagramNode, Dimension initialSize) {
		super(null, parentDiagramNode, null);
		this.width = initialSize.width;
		this.height = initialSize.height;
//		LabelModelNode someLabel = new LabelModelNode("JUNK LABEL");
//		if (children == null) {
//			children = new ArrayList();
//		}
//		children.add(someLabel);
	}
	    
	@Override
    public String toString() {
		return "FocusModelNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}


}
