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

package com.metamatrix.modeler.relationship.ui.actions;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.internal.ui.actions.ModelObjectAction;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipAction extends ModelObjectAction   {

	private Diagram currentDiagram;

	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Construct an instance of RelationshipAction.
	 * @param thePlugin
	 */
	public RelationshipAction() {
		super(UiPlugin.getDefault());
	}

	/**
	 * Construct an instance of RelationshipAction.
	 * @param thePlugin
	 * @param theStyle
	 */
	public RelationshipAction(int theStyle) {
		super(UiPlugin.getDefault(), theStyle );
	}
	
	public RelationshipAction(Diagram diagram) {
		super(UiPlugin.getDefault());
		this.currentDiagram = diagram;
	}
    
	public void setDiagram(Diagram diagram ) {
		this.currentDiagram = diagram;
	}
    

	@Override
    protected void doRun() {
	}
    
	protected boolean isRelationshipDiagram() {
		if( currentDiagram!= null &&
			currentDiagram.getType()!= null &&
			currentDiagram.getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID))
			return true;
            
		return false;
	}
    
	protected boolean isWritable(EObject eObject) {
		return ! ModelObjectUtilities.isReadOnly(eObject);
	}
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
	 */
	@Override
    protected boolean requiresEditorForRun() {
		return false;
	}
}
