/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.actions;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.ui.actions.IModelObjectActionContributor;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipDiagramPermanentActionContributor implements IModelObjectActionContributor {
	    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
	    
	private ShowRelationshipDiagramAction showDiagramAction;
	    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
	    
	public RelationshipDiagramPermanentActionContributor() {
		initActions();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////
	    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectActionContributor#contributeToContextMenu(org.eclipse.jface.action.IMenuManager, org.eclipse.jface.viewers.ISelection)
	 */
	public void contributeToContextMenu(IMenuManager theMenuMgr, ISelection theSelection) {
		        
		// Need to check the selection first.
	        
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectActionContributor#contributeToEditMenu(org.eclipse.jface.action.IMenuManager, org.eclipse.jface.viewers.ISelection)
	 */
	public void contributeToEditMenu(IMenuManager theMenuMgr, ISelection theSelection) {
		// Need to check the selection first.
	}
    
    /**
     *  
     * @see com.metamatrix.modeler.ui.actions.IModelObjectActionContributor#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions(ISelection theSelection) {
        List addedActions = new ArrayList();
        
        // Need to check the selection first.
        
        if( SelectionUtilities.isSingleSelection(theSelection) ) {
            EObject selectedObject = SelectionUtilities.getSelectedEObject(theSelection);
            if( selectedObject != null ) {
                if( showDiagramAction.isEnabled() ) {
                    addedActions.add(showDiagramAction);
                }
            }
        }
        
        return addedActions;
    }
	    
	/**
	 * Construct and register actions.
	 */
	private void initActions() {
		showDiagramAction = new ShowRelationshipDiagramAction();
		DiagramUiPlugin.registerDiagramActionForSelection(showDiagramAction);
	}

}
