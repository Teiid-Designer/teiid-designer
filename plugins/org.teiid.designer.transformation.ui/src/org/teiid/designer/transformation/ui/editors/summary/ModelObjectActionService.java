/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.summary;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.handlers.IWorkbenchWindowHandlerDelegate;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.actions.CloneAction;
import org.teiid.designer.ui.actions.CopyAction;
import org.teiid.designer.ui.actions.CutAction;
import org.teiid.designer.ui.actions.DeleteAction;
import org.teiid.designer.ui.actions.ModelerActionBarIdManager;
import org.teiid.designer.ui.actions.ModelerActionService;
import org.teiid.designer.ui.actions.ModelerSpecialActionManager;
import org.teiid.designer.ui.actions.NewSiblingAction;
import org.teiid.designer.ui.actions.PasteAction;
import org.teiid.designer.ui.actions.RenameAction;
import org.teiid.designer.ui.actions.IModelerActionConstants.ContextMenu;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;

public class ModelObjectActionService implements ISelectionChangedListener, ModelActionConstants {

	DeleteAction delete;
	CutAction cut;
	CopyAction copy;
	PasteAction paste;
	CloneAction clone;
	RenameAction rename;
	
	final ModelerActionService actionService;
	final IWorkbenchPart part;

	
	public ModelObjectActionService(ModelerActionService actionService, IWorkbenchPart part) {
		super();
		
		this.actionService = actionService;
		this.part = part;

		init();
	}

	private void init() {
		delete = new DeleteAction();
		cut = new CutAction();
		copy = new CopyAction();
		paste = new PasteAction();
		clone = new CloneAction(UiPlugin.getDefault().getImage(UiConstants.Images.CLONE_ICON), 
				UiPlugin.getDefault().getImage(UiConstants.Images.CLONE_DISABLED_ICON) );
		rename = new RenameAction();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		delete.selectionChanged(part, event.getSelection());
		cut.selectionChanged(part, event.getSelection());
		copy.selectionChanged(part, event.getSelection());
		paste.selectionChanged(part, event.getSelection());
		clone.selectionChanged(part, event.getSelection());
		rename.selectionChanged(part, event.getSelection());
	}

    public void contributeToContextMenu( IMenuManager theMenuMgr, ISelection theSelection ) {
        //
        // Menu item group for insert child and sibling
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.INSERT_START));
        theMenuMgr.add(actionService.getInsertChildMenu(theSelection));
        
        IMenuManager mgr = actionService.getInsertSiblingMenu(theSelection);
        if( !mgr.isEmpty() && mgr.getItems().length > 1) {
        	theMenuMgr.add(mgr);
        } else if( !mgr.isEmpty() ) {
        	boolean addIt = true;
        	IContributionItem[] items = mgr.getItems();
        	IAction action = ((ActionContributionItem)items[0]).getAction();
        	if(action instanceof NewSiblingAction ) {
	        	if( action.getText().equals("(none allowed)")) {
	        		addIt = false;
	        	}
        	} 
        	
        	if( addIt ) {
        		theMenuMgr.add(mgr);
        	}
        }
        
        IMenuManager modelingActionMenu = getModelingActionMenu(theSelection);
        if (modelingActionMenu != null && modelingActionMenu.getItems().length > 0) {
        	theMenuMgr.add(new Separator());
            theMenuMgr.add(modelingActionMenu);
        }
        
        theMenuMgr.add(new Separator());

        theMenuMgr.add(cut);
        theMenuMgr.add(copy);
        theMenuMgr.add(paste);
        theMenuMgr.add(clone);
        
        theMenuMgr.add(new Separator());
        theMenuMgr.add(delete);
        theMenuMgr.add(rename);

    }
    
    /**
     * Allows access to a full modeling action menu based on a supplied selection
     * 
     * @param theSelection the selection object
     * @return the menu manager
     * @since 5.0
     */
    public MenuManager getModelingActionMenu( ISelection theSelection ) {
        MenuManager menu = new MenuManager(MODELING_LABEL, ModelerActionBarIdManager.getModelingMenuId());

        MenuManager mosaMenu = ModelerSpecialActionManager.getModeObjectSpecialActionMenu(theSelection);
        if (mosaMenu != null && mosaMenu.getItems().length > 0) {
            Object[] items = mosaMenu.getItems();
            for (int i = 0; i < items.length; i++) {
                menu.add(mosaMenu.getItems()[i]);
            }
            menu.add(new Separator());
        }
        
        EObject eObj = SelectionUtilities.getSelectedEObject(theSelection);
        Collection<IAction> actions = ModelEditorHyperlinkManager.getCustomActions(eObj);
        if( !actions.isEmpty() ) {
	        for( IAction action : actions ) {
	        	menu.add(action);
	        }
	        menu.add(new Separator());
        }
        
        return menu;
    }
}
