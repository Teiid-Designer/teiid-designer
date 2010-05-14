/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.layout;

import java.util.List;
import com.metamatrix.modeler.diagram.ui.layout.DiagramLayout;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipLayout extends DiagramLayout {

	/**
	 * 
	 */
	public RelationshipLayout() {
		super();
		// XXX Auto-generated constructor stub
	}

	/**
	 * @param newNodes
	 */
	public RelationshipLayout(List newNodes) {
		super(newNodes);
		// XXX Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.layout.DiagramLayout#run()
	 */
	@Override
    protected int run() {
		
		return runLayout();
	}
	
	private int runLayout() {
		
		return SUCCESSFUL;
	}

}
