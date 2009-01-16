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

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.emf.common.notify.Notification;

import com.metamatrix.modeler.diagram.ui.actions.DiagramGlobalActionsMap;
import com.metamatrix.modeler.diagram.ui.actions.SaveDiagramAction;
import com.metamatrix.modeler.diagram.ui.drawing.actions.RefreshAction;
import com.metamatrix.modeler.diagram.ui.editor.DiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.pakkage.actions.RenameAction;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.GlobalActionsMap;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipDiagramActionAdapter
	 extends DiagramActionAdapter
  implements UiConstants, 
			 IRelationshipDiagramActionConstants, 
			 IModelerActionConstants {

	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	/////////////////////////////////////////////////////////////////////////////////////////////// 

	//============================================================================================================================
	// Variables

	private RefreshAction refreshDiagramAction;
	private ShowParentDiagramAction upPackageDiagramAction;
	private RemoveFromRelationshipAction removeFromRelationshipAction;
	private AbstractAction saveDiagramAction;
	
	// overrides of global actions
	private AbstractAction deleteAction;
	private AbstractAction cutAction;
	private AbstractAction copyAction;
	private AbstractAction pasteAction;
	private AbstractAction cloneAction;
	private RenameAction renameAction;

	// actions map is needed since we want to override the default print action
	private GlobalActionsMap actionsMap;

	//============================================================================================================================
	// Constructors

	/**
	 * @since 4.0
	 */
	public RelationshipDiagramActionAdapter(final ModelEditorPage page) {
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
			// construct a global actions map that contains this adapter's tweaks
			actionsMap = new DiagramGlobalActionsMap();
			actionsMap.reset();
            
			actionsMap.put( EclipseGlobalActions.DELETE, this.deleteAction );            
			actionsMap.put( EclipseGlobalActions.CUT, this.cutAction );            
			actionsMap.put( EclipseGlobalActions.COPY, this.copyAction );            
			actionsMap.put( EclipseGlobalActions.PASTE, this.pasteAction );                         
			actionsMap.put( IModelerActionConstants.ModelerGlobalActions.CLONE, this.cloneAction );
			this.renameAction.setDiagramEditor((DiagramEditor)getEditorPage());
			actionsMap.put( EclipseGlobalActions.RENAME, this.renameAction );                  
		}

		return actionsMap;
	}

	/**
	 * @since 4.0
	 */
	@Override
    protected void initActions() {
        super.initActions();
        
		//----- DeleteAction -----//
        
        this.deleteAction = (AbstractAction)getRegisteredAction(DeleteAction.class.getName());
        
		if (this.deleteAction == null) {
            this.deleteAction = new DeleteAction();
			registerAction(this.deleteAction);
		}

		//----- CutAction -----//
        
        this.cutAction = (AbstractAction)getRegisteredAction(CutAction.class.getName());
        
		if (this.cutAction == null) {
            this.cutAction = new CutAction();
			registerAction(this.cutAction);
		}

		//----- CopyAction -----//
        
        this.copyAction = (AbstractAction)getRegisteredAction(CopyAction.class.getName());
        
		if (this.copyAction == null) {
            this.copyAction = new CopyAction();
			registerAction(this.copyAction);
		}

		//----- PasteAction -----//
        
        this.pasteAction = (AbstractAction)getRegisteredAction(PasteAction.class.getName());
        
		if (this.pasteAction == null) {
            this.pasteAction = new PasteAction();
			registerAction(this.pasteAction);
		}

		//----- CloneAction -----//
        
        this.cloneAction = (AbstractAction)getRegisteredAction(CloneAction.class.getName());
        
		if (this.cloneAction == null) {
            this.cloneAction = new CloneAction();
			registerAction(this.cloneAction);
		}
		
        this.renameAction = (RenameAction)getRegisteredAction(RenameAction.class.getName());
        
		if (this.renameAction == null) {
            this.renameAction = new RenameAction();
			registerAction(this.renameAction);
		}
        
//		  action = (AbstractAction)getRegisteredAction(NewRectangleAction.class.getName());
//
//		  if (action == null) {
//			  action = new NewRectangleAction();
//			  registerAction(action);
//		  }
//        
//		  this.newRectangleACI = createActionContributionItem(action);
//        
//		  //----- NewEllipseAction -----//
//
//		  action = (AbstractAction)getRegisteredAction(NewEllipseAction.class.getName());
//        
//		  if (action == null) {
//			  action = new NewEllipseAction();
//			  registerAction(action);
//		  }
//        
//		  this.newEllipseACI = createActionContributionItem(action);
//        
//		  //----- NewTextAction -----//
//
//		  action = (AbstractAction)getRegisteredAction(NewTextAction.class.getName());
//        
//		  if (action == null) {
//			  action = new NewTextAction();
//			  registerAction(action);
//		  }
//        
//		  this.newTextACI = createActionContributionItem(action);
//        
//		  //----- NewNoteAction -----//
//
//		  action = (AbstractAction)getRegisteredAction(NewNoteAction.class.getName());
//        
//		  if (action == null) {
//			  action = new NewNoteAction();
//			  registerAction(action);
//		  }
//        
//		  this.newNoteACI = createActionContributionItem(action);
        
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
		
		//		----- RemoveFromRelationshipAction -----//

        this.removeFromRelationshipAction = (RemoveFromRelationshipAction)getRegisteredAction(RemoveFromRelationshipAction.class.getName());
        
		if (this.removeFromRelationshipAction == null) {
            this.removeFromRelationshipAction = new RemoveFromRelationshipAction();
			registerAction(this.removeFromRelationshipAction);
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
		createContextMenu(IRelationshipDiagramActionConstants.ContextMenu.DIAGRAM_EDITOR_PAGE, getEditorPage().getControl());
	}
    
	@Override
    public void contributeToMenuManager(IMenuManager theMenuMgr, ISelection selection) {
		// clean up first
		if ( theMenuMgr.find( IRelationshipDiagramActionConstants.ContextMenu.DIAGRAM_START ) != null )
			theMenuMgr.remove( IRelationshipDiagramActionConstants.ContextMenu.DIAGRAM_START );
		if ( theMenuMgr.find( this.refreshDiagramAction.getId() ) != null )
			theMenuMgr.remove( this.refreshDiagramAction.getId() );
		if ( theMenuMgr.find( this.upPackageDiagramAction.getId() ) != null )
			theMenuMgr.remove( this.upPackageDiagramAction.getId() );
		if ( theMenuMgr.find( this.removeFromRelationshipAction.getId() ) != null )
			theMenuMgr.remove( this.removeFromRelationshipAction.getId() );
		if ( theMenuMgr.find( this.saveDiagramAction.getId() ) != null )
			theMenuMgr.remove( this.saveDiagramAction.getId() );
		if ( theMenuMgr.find( IRelationshipDiagramActionConstants.ContextMenu.DIAGRAM_END ) != null )
			theMenuMgr.remove( IRelationshipDiagramActionConstants.ContextMenu.DIAGRAM_END );
                    
		// now add the actions                
		theMenuMgr.add(new GroupMarker(IRelationshipDiagramActionConstants.ContextMenu.DIAGRAM_START));
		theMenuMgr.insertAfter(IModelerActionConstants.ContextMenu.ADDITIONS, this.removeFromRelationshipAction);
		theMenuMgr.insertAfter(this.removeFromRelationshipAction.getId(), this.refreshDiagramAction);
		theMenuMgr.insertAfter(this.refreshDiagramAction.getId(), this.upPackageDiagramAction);
		theMenuMgr.insertAfter(this.upPackageDiagramAction.getId(), this.saveDiagramAction);

		theMenuMgr.add(new GroupMarker(IRelationshipDiagramActionConstants.ContextMenu.DIAGRAM_END));
		theMenuMgr.add(new Separator());
	}
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
    public void contributeExportedActions(IMenuManager theMenuMgr) {
		// nothing to add to the main menu
//      if ((theMenuMgr.getId() != null) && theMenuMgr.getId().equals(ModelerActionBarIdManager.getEditMenuId())) {
//      // edit menu contributions should only happen one time (contributeToMenu(IMenuManager)). 
//      // call createActionContributionItem so that the contributions visibility can be controlled
//      // by the pageActivate/pageDeactivate methods.
//  } else {
//      // assume it must be a context menu. just add to end.
//  }
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
        // NOTE: this method gets called each time the relationship diagram is displayed
		ToolBarManager tbm = ((DiagramEditor)getEditorPage()).getToolBarManager();

		tbm.removeAll();
		tbm.add(this.refreshDiagramAction);
		tbm.add(this.upPackageDiagramAction);

		tbm.add(new Separator());
		tbm.add(this.removeFromRelationshipAction);
		
		tbm.add(new Separator());
		tbm.add(this.saveDiagramAction);
        
		this.refreshDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
		this.upPackageDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
		this.removeFromRelationshipAction.setDiagramEditor((DiagramEditor)getEditorPage());
        
		tbm.update(true);
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
