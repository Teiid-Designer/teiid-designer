/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom.actions;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.DiagramGlobalActionsMap;
import com.metamatrix.modeler.diagram.ui.actions.DiagramPageSetupAction;
import com.metamatrix.modeler.diagram.ui.actions.RouterTypeMenuManager;
import com.metamatrix.modeler.diagram.ui.actions.SaveDiagramAction;
import com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.drawing.actions.RefreshAction;
import com.metamatrix.modeler.diagram.ui.editor.DiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramToolBarManager;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.pakkage.actions.IPackageDiagramActionConstants;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.actions.ModelerSpecialActionManager;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.GlobalActionsMap;
import com.metamatrix.ui.actions.IActionConstants.EclipseGlobalActions;
/**
 * CustomDiagramActionAdapter
 */
public class CustomDiagramActionAdapter
     extends DiagramActionAdapter
  implements ICustomDiagramActionConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    /////////////////////////////////////////////////////////////////////////////////////////////// 

    private static final String D_MARKER = IPackageDiagramActionConstants.ContextMenu.DIAGRAM_START;
    private static final String D_MARKER_END = IPackageDiagramActionConstants.ContextMenu.DIAGRAM_END;


    //============================================================================================================================
    // Variables

    private AbstractAction addToNewCustomDiagramAction;
    private AddToDiagramAction addAction;
    private RemoveFromDiagramAction removeAction;
    private ClearDiagramAction clearAction;
    private AddAssociatedObjectsAction addAssociatedAction;
    
    private MenuManager linkTypeManager;

	private RefreshAction refreshDiagramAction;
	private ShowParentDiagramAction upPackageDiagramAction;
    private AbstractAction saveDiagramAction;
    private AbstractAction diagramPageSetupAction;
    private AbstractAction showPageGridAction;
    
    // overrides of global actions
    private AbstractAction deleteAction;
    private AbstractAction cutAction;
    private AbstractAction pasteAction;
    private AbstractAction cloneAction;

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
    
    private boolean singleLinkSelected() {
        List selectedEPs = getSelectedInDiagram();
        if( ! selectedEPs.isEmpty() && selectedEPs.size() == 1) {
            Object selectedEP = selectedEPs.get(0);
            if( selectedEP instanceof NodeConnectionEditPart )
                return true;
        }
        
        return false;
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
                        
            actionsMap.put( EclipseGlobalActions.DELETE, this.deleteAction );            
            actionsMap.put( EclipseGlobalActions.CUT, this.cutAction );            
//            actionsMap.put( EclipseGlobalActions.COPY, aciCopyAction.getAction() );          
            actionsMap.put( EclipseGlobalActions.PASTE, this.pasteAction );                         
            actionsMap.put( IModelerActionConstants.ModelerGlobalActions.CLONE, this.cloneAction );        
        }

        // PAF, 2004-12-07 - quick hack: enable/disable Link Types menu dynamically.
        updateLinkTypeMenu();

        return actionsMap;
    }

    /**
     * Based on readonly status, update the enabled state of the link type submenu.
     */
    private void updateLinkTypeMenu() {
        DiagramModelNode currentModel = getDiagramEditor().getCurrentModel();
        if (currentModel != null) {
            boolean readOnly = currentModel.isReadOnly();
            ((RouterTypeMenuManager)linkTypeManager).setEnabled(!readOnly);
        } // endif
    }

    /**
     * @since 4.0
     */
    @Override
    protected void initActions() {
        super.initActions();
        
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
        
        //----- LinkSelectionAction -----//

        linkTypeManager = new RouterTypeMenuManager();

        //----- AddToNewCustomDiagramAction -----//
        
        this.addToNewCustomDiagramAction = (AbstractAction)getRegisteredAction(AddToNewCustomDiagramAction.class.getName());
        
        if (this.addToNewCustomDiagramAction == null) {
            this.addToNewCustomDiagramAction = new AddToNewCustomDiagramAction();
            registerAction(this.addToNewCustomDiagramAction);
        }
        
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
        
        //----- AddAssociatedObjectsAction -----//
        
        this.addAssociatedAction = (AddAssociatedObjectsAction)getRegisteredAction(AddAssociatedObjectsAction.class.getName());
        
        if (this.addAssociatedAction == null) {
            this.addAssociatedAction = new AddAssociatedObjectsAction();
            registerAction(this.addAssociatedAction);
        }
        
        //----- SaveDiagramAction -----//
        
        this.saveDiagramAction = (AbstractAction)getRegisteredAction(SaveDiagramAction.class.getName());
        
        if (this.saveDiagramAction == null) {
            this.saveDiagramAction = new SaveDiagramAction((DiagramEditor)this.getEditorPage());
            registerAction(this.saveDiagramAction);
        }
        
        //----- DiagramPageSetupAction -----//
        
        this.diagramPageSetupAction = (AbstractAction)getRegisteredAction(DiagramPageSetupAction.class.getName());
        
        if (this.diagramPageSetupAction == null) {
            this.diagramPageSetupAction = new DiagramPageSetupAction((DiagramEditor)this.getEditorPage());
            registerAction(this.diagramPageSetupAction);
        }        
        
        //----- ShowPageGridAction -----//
        
        this.showPageGridAction = (AbstractAction)getRegisteredAction(ShowPageGridAction.class.getName());
        
        if (this.showPageGridAction == null) {
            this.showPageGridAction = new ShowPageGridAction((DiagramEditor)this.getEditorPage());
            registerAction(this.showPageGridAction);
        }        
        
        // GLOBAL ACTIONS
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

//        action = new CopyAction();
//        DiagramUiPlugin.registerDiagramActionForSelection((DiagramAction)action);
//        this.aciCopyAction = createActionContributionItem(action);

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
    
    private void clearAllActions(IMenuManager theMenuMgr) {
        IContributionItem[] allContributions = theMenuMgr.getItems();
        List removeItems = new ArrayList(allContributions.length);
        
        for(int i=0; i<allContributions.length; i++ ) {
            IContributionItem nextItem = allContributions[i];
            if( !(nextItem instanceof GroupMarker) && !(nextItem instanceof Separator) ) {
                removeItems.add(nextItem);
            }
        }
        
        Iterator iter = removeItems.iterator();
        while( iter.hasNext() ) {
            theMenuMgr.remove((IContributionItem)iter.next());
        }
    }
    
    @Override
    public void contributeToMenuManager(IMenuManager theMenuMgr, ISelection selection) {
        if( singleLinkSelected() ) {
            clearAllActions(theMenuMgr);
//            // now add the actions
            ((RouterTypeMenuManager)linkTypeManager).setSingleLinkEdit(true);
            ((RouterTypeMenuManager)linkTypeManager).setInitialSelection();
            addDiagramActions(theMenuMgr);
//            theMenuMgr.add(linkTypeManager);
        } else {    
            // clean up first
            if ( theMenuMgr.find( ContextMenu.DIAGRAM_START ) != null )
                theMenuMgr.remove( ContextMenu.DIAGRAM_START );
            if ( theMenuMgr.find( addAssociatedAction.getId() ) != null )
                theMenuMgr.remove( addAssociatedAction.getId() );
            if ( theMenuMgr.find( removeAction.getId() ) != null )
                theMenuMgr.remove( removeAction.getId() );
            if ( theMenuMgr.find( clearAction.getId() ) != null )
                theMenuMgr.remove( clearAction.getId() );
                
    		if ( theMenuMgr.find( this.refreshDiagramAction.getId() ) != null )
    			theMenuMgr.remove( this.refreshDiagramAction.getId() );
    		if ( theMenuMgr.find( this.upPackageDiagramAction.getId() ) != null )
    			theMenuMgr.remove( this.upPackageDiagramAction.getId() );
            if ( theMenuMgr.find( this.saveDiagramAction.getId() ) != null )
                theMenuMgr.remove( this.saveDiagramAction.getId() );
            if ( theMenuMgr.find( this.diagramPageSetupAction.getId() ) != null )
                theMenuMgr.remove( this.diagramPageSetupAction.getId() );
            if ( theMenuMgr.find( this.showPageGridAction.getId() ) != null )
                theMenuMgr.remove( this.showPageGridAction.getId() );
            
            if ( theMenuMgr.find( ContextMenu.DIAGRAM_END ) != null )
                theMenuMgr.remove( ContextMenu.DIAGRAM_END );
            
            MenuManager menuMgr = getModelingActionMenu(selection);
            if(menuMgr.getItems().length > 0 ) {
                theMenuMgr.add(menuMgr);
                theMenuMgr.add(new Separator());
            }
            
            addDiagramActions(theMenuMgr);
        }
    }
        
    private void addDiagramActions(IMenuManager theMenuMgr) {
        // now add the actions                
        theMenuMgr.add(new GroupMarker(D_MARKER));
        theMenuMgr.appendToGroup(D_MARKER, this.refreshDiagramAction);
        theMenuMgr.appendToGroup(D_MARKER, this.upPackageDiagramAction);
        theMenuMgr.appendToGroup(D_MARKER, this.addAssociatedAction);
        theMenuMgr.appendToGroup(D_MARKER, this.removeAction);
        theMenuMgr.appendToGroup(D_MARKER, this.clearAction);
        theMenuMgr.appendToGroup(D_MARKER, this.linkTypeManager);
        theMenuMgr.appendToGroup(D_MARKER, this.saveDiagramAction);
        theMenuMgr.appendToGroup(D_MARKER, this.diagramPageSetupAction);
        theMenuMgr.appendToGroup(D_MARKER, this.showPageGridAction);

        theMenuMgr.add(new Separator());
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void contributeExportedActions(IMenuManager theMenuMgr) {
        IContributionItem groupMarker = new Separator(D_MARKER);
        
        // check to see if menu is edit menu or just a context menu
        if ((theMenuMgr.getId() != null) && theMenuMgr.getId().equals(ModelerActionBarIdManager.getEditMenuId())) {
            setEditMenu(theMenuMgr); // need this in dispose()

            // edit menu contributions should only happen one time (contributeToMenu(IMenuManager)). 
            // call createActionContributionItem so that the contributions visibility can be controlled
            // by the pageActivate/pageDeactivate methods.
            theMenuMgr.appendToGroup(ModelerActionBarIdManager.getMenuAdditionsMarkerId(), groupMarker);
            addContributionItem(groupMarker);
            
            theMenuMgr.appendToGroup(D_MARKER, createActionContributionItem(this.addToNewCustomDiagramAction));
            theMenuMgr.appendToGroup(D_MARKER, createActionContributionItem(this.addAction));
            theMenuMgr.appendToGroup(D_MARKER, createActionContributionItem(this.clearAction));
            
            Separator sep = new Separator(D_MARKER_END);
            addContributionItem(sep);
            theMenuMgr.appendToGroup(D_MARKER, sep);
        } else {
            // assume it must be a context menu. just add to end.
            theMenuMgr.add(groupMarker);
            theMenuMgr.appendToGroup(D_MARKER, this.addToNewCustomDiagramAction);
            theMenuMgr.appendToGroup(D_MARKER, this.addAction);
            theMenuMgr.appendToGroup(D_MARKER, this.clearAction);
            theMenuMgr.appendToGroup(D_MARKER, new Separator(D_MARKER_END));
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
        // NOTE: this method gets called each time the custom diagram is displayed
        DiagramToolBarManager tbm = (DiagramToolBarManager)((DiagramEditor)getEditorPage()).getToolBarManager();

        tbm.removeAll();
		tbm.add(this.refreshDiagramAction);
		tbm.add(this.upPackageDiagramAction);
		tbm.add(new Separator());
        
        IAction previewAction = ModelerSpecialActionManager.getAction(com.metamatrix.modeler.ui.UiConstants.Extensions.PREVIEW_DATA_ACTION_ID);
        if( previewAction != null ) {
            tbm.add(previewAction);
            tbm.add(new Separator());
        }
        
        tbm.add(this.addAction);
        tbm.add(this.removeAction);
        tbm.add(this.clearAction);
        
        tbm.add(new Separator());
        tbm.add(this.saveDiagramAction);
        tbm.add(this.diagramPageSetupAction);
        tbm.add(this.showPageGridAction);
        
		this.refreshDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
		this.upPackageDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.clearAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.removeAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.addAssociatedAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.addAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.addAction.setToolBarManager(tbm);
        
        ActionContributionItem addItem = new ActionContributionItem(this.addAction);
        this.addAction.setItem(addItem);

        tbm.update(true);
    }
    
    
    private MenuManager getModelingActionMenu(ISelection theSelection) {
        IWorkbenchWindow window = DiagramUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        ModelerActionService service = (ModelerActionService)com.metamatrix.modeler.ui.UiPlugin.getDefault().getActionService(window.getActivePage());
        return service.getModelingActionMenu(theSelection);
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#shouldOverrideMenu()
     * @since 4.2
     */
    @Override
    public boolean shouldOverrideMenu(ISelection selection) {
        boolean value = false;
        if( singleLinkSelected() ) {
            return true;
        } else if( !singleLinkSelected() ){
            // This is where we check for other cases.....
            value = true;
            
        }
        
        return value;
    }
}

