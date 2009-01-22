/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
