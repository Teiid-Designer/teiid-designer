/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom.actions;


import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.emf.common.notify.Notification;

import com.metamatrix.modeler.diagram.ui.actions.DiagramGlobalActionsMap;
import com.metamatrix.modeler.diagram.ui.actions.SaveDiagramAction;
import com.metamatrix.modeler.diagram.ui.drawing.actions.RefreshAction;
import com.metamatrix.modeler.diagram.ui.editor.DiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramToolBarManager;
import com.metamatrix.modeler.relationship.ui.actions.ShowParentDiagramAction;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.GlobalActionsMap;
/**
 * CustomDiagramActionAdapter
 */
public class CustomDiagramActionAdapter
     extends DiagramActionAdapter
  implements ICustomDiagramActionConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////////////////////////////////////////////////// 


    //============================================================================================================================
    // Variables
	private RefreshAction refreshDiagramAction;
	private ShowParentDiagramAction upPackageDiagramAction;
	
    private AddToDiagramAction addAction;
    private RemoveFromDiagramAction removeAction;
    private ClearDiagramAction clearAction;
//    private ActionContributionItem restoreRelationshipACI;

	private AddSubTypesAction addSubTypesAction;
	private AddSuperTypeAction addSuperTypeAction;
    
    private AbstractAction saveDiagramAction;
 

    // actions map is needed since we want to override the default print action
    private GlobalActionsMap actionsMap;

    //============================================================================================================================
    // Constructors

    /**
     * @since 4.0
     */
    public CustomDiagramActionAdapter(final ModelEditorPage page) {
        super(page);
    }

    //============================================================================================================================
    // Methods

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#getGlobalActions()
     */
    @Override
    public GlobalActionsMap getGlobalActions() {
        if ( actionsMap == null ) {
            actionsMap = new DiagramGlobalActionsMap();
            actionsMap.reset();    
        }
        return actionsMap;
    }

    /**
     * @since 4.0
     */
    @Override
    protected void initActions() {
        super.initActions();
        
        //----- AddToDiagramAction -----//
        
        this.addAction = (AddToDiagramAction)getRegisteredAction(AddToDiagramAction.class.getName());
        
        if (this.addAction == null) {
            this.addAction = new AddToDiagramAction();
            registerAction(this.addAction);
        }

        //----- RemoveFromDiagramAction -----//
        
        this.removeAction = (RemoveFromDiagramAction)getRegisteredAction(RemoveFromDiagramAction.class.getName());
        
        if (this.removeAction == null) {
            this.removeAction = new RemoveFromDiagramAction();
            registerAction(this.removeAction);
        }
        
        //----- ClearDiagramAction -----//
        
        this.clearAction = (ClearDiagramAction)getRegisteredAction(ClearDiagramAction.class.getName());
        
        if (this.clearAction == null) {
            this.clearAction = new ClearDiagramAction();
            registerAction(this.clearAction);
        }
        
		//----- AddSuperTypeAction -----//
        
        this.addSuperTypeAction = (AddSuperTypeAction)getRegisteredAction(AddSuperTypeAction.class.getName());
        
		if (this.addSuperTypeAction == null) {
            this.addSuperTypeAction = new AddSuperTypeAction();
			registerAction(this.addSuperTypeAction);
		}
		
		//----- AddSubTypesAction -----//
        
        this.addSubTypesAction = (AddSubTypesAction)getRegisteredAction(AddSubTypesAction.class.getName());
        
		if (this.addSubTypesAction == null) {
            this.addSubTypesAction = new AddSubTypesAction();
			registerAction(this.addSubTypesAction);
		}
        
//		//----- RestoreRelationshipAction -----//
//        
//		action = (AbstractAction)getRegisteredAction(RestoreRelationshipAction.class.getName());
//        
//		if (action == null) {
//			action = new RestoreRelationshipAction();
//			registerAction(action);
//		}
//        
//		this.restoreRelationshipACI = createActionContributionItem(action);
        
//        //----- NewRectangleAction -----//
//        
//        action = (AbstractAction)getRegisteredAction(NewRectangleAction.class.getName());
//        
//        if (action == null) {
//            action = new NewRectangleAction();
//            registerAction(action);
//        }
//        
//        this.newRectangleACI = createActionContributionItem(action);
//        
//        //----- NewEllipseAction -----//
//        
//        action = (AbstractAction)getRegisteredAction(NewEllipseAction.class.getName());
//        
//        if (action == null) {
//            action = new NewEllipseAction();
//            registerAction(action);
//        }
//        
//        this.newEllipseACI = createActionContributionItem(action);
//        
//        //----- NewTextAction -----//
//        
//        action = (AbstractAction)getRegisteredAction(NewTextAction.class.getName());
//        
//        if (action == null) {
//            action = new NewTextAction();
//            registerAction(action);
//        }
//        
//        this.newTextACI = createActionContributionItem(action);
//        
//        //----- NewNoteAction -----//
//        
//        action = (AbstractAction)getRegisteredAction(NewNoteAction.class.getName());
//        
//        if (action == null) {
//            action = new NewNoteAction();
//            registerAction(action);
//        }
//        
//        this.newNoteACI = createActionContributionItem(action);

		//----- RefreshAction -----//

        this.refreshDiagramAction = (RefreshAction)getRegisteredAction(RefreshAction.class.getName());
        
		if (this.refreshDiagramAction == null) {
            this.refreshDiagramAction = new RefreshAction();
			registerAction(this.refreshDiagramAction);
		}

		//----- ShowParentDiagramAction -----//

        this.upPackageDiagramAction = (ShowParentDiagramAction)getRegisteredAction(ShowParentDiagramAction.class.getName());
        
		if (this.upPackageDiagramAction == null) {
            this.upPackageDiagramAction = new ShowParentDiagramAction();
			registerAction(this.upPackageDiagramAction);
		}
        
        //----- SaveDiagramAction -----//
        
        this.saveDiagramAction = (AbstractAction)getRegisteredAction(SaveDiagramAction.class.getName());
        
        if (this.saveDiagramAction == null) {
            this.saveDiagramAction = new SaveDiagramAction((DiagramEditor)this.getEditorPage());
            registerAction(this.saveDiagramAction);
        }
    }

    //============================================================================================================================
    // AbstractModelEditorPageActionBarContributor Methods

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
     */
    @Override
    public void createContextMenu() {
        createContextMenu(ContextMenu.DIAGRAM_EDITOR_PAGE, getEditorPage().getControl());
    }
    
    @Override
    public void contributeToMenuManager(IMenuManager theMenuMgr, ISelection selection) {
//System.out.println("[CustomDiagramActionAdapter] TOP, About to add 'remove' and 'clear'"); //$NON-NLS-1$        
        
        // clean up first
        if ( theMenuMgr.find( ContextMenu.DIAGRAM_START ) != null )
            theMenuMgr.remove( ContextMenu.DIAGRAM_START );
        if ( theMenuMgr.find( removeAction.getId() ) != null )
            theMenuMgr.remove( removeAction.getId() );
        if ( theMenuMgr.find( clearAction.getId() ) != null )
            theMenuMgr.remove( clearAction.getId() );
		if ( theMenuMgr.find( addSubTypesAction.getId() ) != null )
			theMenuMgr.remove( addSubTypesAction.getId() );
		if ( theMenuMgr.find( addSuperTypeAction.getId() ) != null )
			theMenuMgr.remove( addSuperTypeAction.getId() );
//		if ( theMenuMgr.find( restoreRelationshipACI.getAction().getId() ) != null )
//			theMenuMgr.remove( restoreRelationshipACI.getAction().getId() );
            
//        if ( theMenuMgr.find( newRectangleACI.getAction().getId() ) != null )
//            theMenuMgr.remove( newRectangleACI.getAction().getId() );
//        if ( theMenuMgr.find( newEllipseACI.getAction().getId() ) != null )
//            theMenuMgr.remove( newEllipseACI.getAction().getId() );
//        if ( theMenuMgr.find( newTextACI.getAction().getId() ) != null )
//            theMenuMgr.remove( newTextACI.getAction().getId() );
//        if ( theMenuMgr.find( newNoteACI.getAction().getId() ) != null )
//            theMenuMgr.remove( newNoteACI.getAction().getId() );
		if ( theMenuMgr.find( this.refreshDiagramAction.getId() ) != null )
			theMenuMgr.remove( this.refreshDiagramAction.getId() );
		if ( theMenuMgr.find( this.upPackageDiagramAction.getId() ) != null )
			theMenuMgr.remove( this.upPackageDiagramAction.getId() );
        if ( theMenuMgr.find( this.saveDiagramAction.getId() ) != null )
            theMenuMgr.remove( this.saveDiagramAction.getId() );
            
        if ( theMenuMgr.find( ContextMenu.DIAGRAM_END ) != null )
            theMenuMgr.remove( ContextMenu.DIAGRAM_END );
                    
        // now add the actions                
        theMenuMgr.add(new GroupMarker(ContextMenu.DIAGRAM_START));
		theMenuMgr.insertAfter(IModelerActionConstants.ContextMenu.ADDITIONS, this.refreshDiagramAction);
		theMenuMgr.insertAfter(this.refreshDiagramAction.getId(), this.upPackageDiagramAction);
		theMenuMgr.insertAfter(this.upPackageDiagramAction.getId(), this.saveDiagramAction);
        theMenuMgr.insertAfter(this.saveDiagramAction.getId(), this.removeAction);
        theMenuMgr.insertAfter(this.removeAction.getId(), this.clearAction);
		theMenuMgr.insertAfter(this.clearAction.getId(), this.addSubTypesAction);
		theMenuMgr.insertAfter(this.addSubTypesAction.getId(), this.addSuperTypeAction);
        
        
//        theMenuMgr.insertAfter(restoreRelationshipACI.getAction().getId(), saveDiagramACI.getAction());
        
//		theMenuMgr.insertAfter(clearAction.getAction().getId(), newRectangleACI.getAction());            
//        theMenuMgr.insertAfter(newRectangleACI.getAction().getId(), newEllipseACI.getAction());
//        theMenuMgr.insertAfter(newEllipseACI.getAction().getId(), newTextACI.getAction());
//        theMenuMgr.insertAfter(newTextACI.getAction().getId(), newNoteACI.getAction());
//        theMenuMgr.insertAfter(newNoteACI.getAction().getId(), saveDiagramACI.getAction());
        
        theMenuMgr.add(new GroupMarker(ContextMenu.DIAGRAM_END));
        theMenuMgr.add(new Separator());
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void contributeExportedActions(IMenuManager theMenuMgr) {
        // now add the actions                
        IContributionItem groupMarker = new Separator(ContextMenu.DIAGRAM_START);
        
        // check to see if menu is edit menu or just a context menu
        if ((theMenuMgr.getId() != null) && theMenuMgr.getId().equals(ModelerActionBarIdManager.getEditMenuId())) {
            setEditMenu(theMenuMgr); // need this in dispose()

            // edit menu contributions should only happen one time (contributeToMenu(IMenuManager)) 
            // call createActionContributionItem so that the contributions visibility can be controlled
            // by the pageActivate/pageDeactivate methods.
            theMenuMgr.appendToGroup(ModelerActionBarIdManager.getMenuAdditionsMarkerId(), groupMarker);
            addContributionItem(groupMarker);
            
            theMenuMgr.appendToGroup(groupMarker.getId(), createActionContributionItem(this.addAction));
            theMenuMgr.insertAfter(DiagramActions.ADD_TO_DIAGRAM, createActionContributionItem(this.clearAction));
            
            Separator sep = new Separator(ContextMenu.DIAGRAM_END);
            addContributionItem(sep);
            theMenuMgr.appendToGroup(groupMarker.getId(), sep);
        } else {
            // assume it must be a context menu. just add to end.
            theMenuMgr.add(groupMarker);
            theMenuMgr.add(this.addAction);
            theMenuMgr.insertAfter(DiagramActions.ADD_TO_DIAGRAM, this.clearAction);
            theMenuMgr.add(new Separator(ContextMenu.DIAGRAM_END));
        }
    }

    //============================================================================================================================
    // EditorActionBarContributor Methods

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#contributeToDiagramToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    @Override
    public void contributeToDiagramToolBar() {
        DiagramToolBarManager tbm = (DiagramToolBarManager)((DiagramEditor)getEditorPage()).getToolBarManager();
		if( tbm != null ) {
	        tbm.removeAll();
			tbm.add(this.refreshDiagramAction);
			tbm.add(this.upPackageDiagramAction);
	
			tbm.add(new Separator());
			
            ActionContributionItem addItem = new ActionContributionItem(this.addAction);
	        tbm.add(addItem);
	        tbm.add(this.removeAction);
	        tbm.add(this.clearAction);
//			tbm.add(restoreRelationshipACI.getAction());
	        
	//        tbm.add(new Separator());
	//        tbm.add(newRectangleACI.getAction());
	//        tbm.add(newEllipseACI.getAction());
	//        tbm.add(newTextACI.getAction());
	//        tbm.add(newNoteACI.getAction());
	        tbm.add(new Separator());
	        tbm.add(this.saveDiagramAction);
	        
	        this.clearAction.setDiagramEditor((DiagramEditor)getEditorPage());
			this.refreshDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
	        this.removeAction.setDiagramEditor((DiagramEditor)getEditorPage());
			this.addSubTypesAction.setDiagramEditor((DiagramEditor)getEditorPage());
			this.addSuperTypeAction.setDiagramEditor((DiagramEditor)getEditorPage());
//			((RestoreRelationshipAction)restoreRelationshipACI.getAction()).setDiagramEditor((DiagramEditor)getEditorPage());
	        this.addAction.setDiagramEditor((DiagramEditor)getEditorPage());
	        this.addAction.setToolBarManager(tbm);
	        this.addAction.setItem(addItem);
	        
	//        ((NewNoteAction)newNoteACI.getAction()).setDiagramEditor((DiagramEditor)getEditorPage());
	//        ((NewEllipseAction)newEllipseACI.getAction()).setDiagramEditor((DiagramEditor)getEditorPage());
	//        ((NewTextAction)newTextACI.getAction()).setDiagramEditor((DiagramEditor)getEditorPage());
	//        ((NewRectangleAction)newRectangleACI.getAction()).setDiagramEditor((DiagramEditor)getEditorPage());
	
	        tbm.update(true);
   		}
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#handleNotification(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public void handleNotification(Notification theNotification) {
        // Currently doesn't need to do anything.
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#enableDiagramToolbarActions()
     */
    @Override
    public void enableDiagramToolbarActions() {
		if( this.upPackageDiagramAction != null )
			this.upPackageDiagramAction.determineEnablement();
    }
    
    /** 
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#shouldOverrideMenu()
     * @since 4.2
     */
    @Override
    public boolean shouldOverrideMenu(ISelection selection) {    
        return false;
    }
}

