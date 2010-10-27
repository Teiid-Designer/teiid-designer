package com.metamatrix.modeler.internal.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;

import com.metamatrix.modeler.internal.ui.favorites.actions.AddToMetadataFavoritesAction;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.IModelObjectActionContributor;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class DesignerPermanentActionContributor implements IModelObjectActionContributor {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private AddToMetadataFavoritesAction addToMetadataFavoritesAction;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public DesignerPermanentActionContributor() {
        initActions();
    }
	@Override
	public void contributeToContextMenu(IMenuManager theMenuMgr,
			ISelection theSelection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IAction> getAdditionalModelingActions(ISelection theSelection) {
        List addedActions = new ArrayList();
        
        // Need to check the selection first.
        if( addToMetadataFavoritesAction.isEnabled() ) {
            if( SelectionUtilities.isAllEObjects(theSelection)  ) {
                addedActions.add(addToMetadataFavoritesAction);
            }
        }
        
        return addedActions;
	}

    /**
     * Construct and register actions.
     */
    private void initActions() {
    	addToMetadataFavoritesAction = new AddToMetadataFavoritesAction();
        UiPlugin.registerActionForSelection(addToMetadataFavoritesAction);
    }
}
