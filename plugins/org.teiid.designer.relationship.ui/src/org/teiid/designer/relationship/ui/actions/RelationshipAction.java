/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.relationship.ui.PluginConstants;
import org.teiid.designer.relationship.ui.UiPlugin;
import org.teiid.designer.ui.actions.ModelObjectAction;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


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
	 * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
	 */
	@Override
    protected boolean requiresEditorForRun() {
		return false;
	}
}
