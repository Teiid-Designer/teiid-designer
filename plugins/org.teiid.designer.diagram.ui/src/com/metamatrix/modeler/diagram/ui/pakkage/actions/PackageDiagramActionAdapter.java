/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.pakkage.actions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import com.metamatrix.modeler.diagram.ui.actions.DiagramGlobalActionsMap;
import com.metamatrix.modeler.diagram.ui.actions.DiagramPageSetupAction;
import com.metamatrix.modeler.diagram.ui.actions.RefreshAction;
import com.metamatrix.modeler.diagram.ui.actions.RouterTypeMenuManager;
import com.metamatrix.modeler.diagram.ui.actions.SaveDiagramAction;
import com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.editor.DiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.diagram.ui.util.RelationalUmlEObjectHelper;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.actions.ModelerSpecialActionManager;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants.ModelerGlobalActions;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.GlobalActionsMap;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
/**
 * TransformationActionAdapter
 */
public class PackageDiagramActionAdapter
     extends DiagramActionAdapter
  implements IPackageDiagramActionConstants, 
             IModelerActionConstants {

     ///////////////////////////////////////////////////////////////////////////////////////////////
     // CONSTANTS
     /////////////////////////////////////////////////////////////////////////////////////////////// 

     private static final String D_MARKER = IPackageDiagramActionConstants.ContextMenu.DIAGRAM_START;
    //============================================================================================================================
    // Variables

    private RefreshAction refreshDiagramAction;
    private ShowParentDiagramAction upPackageDiagramAction;
    private MenuManager linkTypeManager;
    
//	private ActionContributionItem newAssociationACI;

	// overrides of global actions
	private RenameAction renameAction;

    private AbstractAction saveDiagramAction;
    private AbstractAction diagramPageSetupAction;
    private AbstractAction showPageGridAction;

    // actions map is needed since we want to override the default print action
    private GlobalActionsMap actionsMap;

    //============================================================================================================================
    // Constructors

    /**
     * @since 4.0
     */
    public PackageDiagramActionAdapter(final ModelEditorPage page) {
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
    
    private boolean diagramSelected() {
        ISelectionProvider selProvider = getEditorPage().getModelObjectSelectionProvider();
        ISelection selection = selProvider.getSelection();
        if( SelectionUtilities.isSingleSelection(selection)) {
            EObject eObj = SelectionUtilities.getSelectedEObject(selection);
            if( eObj != null && 
                RelationalUmlEObjectHelper.getEObjectType(eObj) == RelationalUmlEObjectHelper.UML_PACKAGE ) {
                // We know a package is selected, now we need to see if the viewer has "nothing seleced"
                if( getDiagramEditor() != null ) {
                    if( getDiagramEditor().getDiagramViewer().getSelectedEditParts().isEmpty() )
                        return true;
                }
                
            }
        }
        return false;
    }
    
    private boolean doRemoveOpen() {
        List selectedEPs = getSelectedInDiagram();
        if( ! selectedEPs.isEmpty() && selectedEPs.size() == 1) {
            Object selectedEP = selectedEPs.get(0);
            if( selectedEP instanceof UmlClassifierEditPart )
                return true;
        }
        
        return false;
    }
    
    private IAction getAction( String theActionId ) {
        IAction action = null;
        try {
            action = getActionService().getAction(theActionId);
        } catch (CoreException err) {
        }

        return action;
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
            this.renameAction.setDiagramEditor((DiagramEditor)getEditorPage());
			actionsMap.put( EclipseGlobalActions.RENAME, this.renameAction );            
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

        //----- RenameAction -----//
        this.renameAction = (RenameAction)getRegisteredAction(RenameAction.class.getName());
		if (this.renameAction == null) {
            this.renameAction = new RenameAction();
			registerAction(this.renameAction);
		}
    }

    //============================================================================================================================
    // AbstractModelEditorPageActionBarContributor Methods

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
     */
    @Override
    public void createContextMenu() {
        createContextMenu(IPackageDiagramActionConstants.ContextMenu.DIAGRAM_EDITOR_PAGE, getEditorPage().getControl());
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
        removeDiagramActions(theMenuMgr);
        
        
        if( diagramSelected() ) {
            addDiagramActions(theMenuMgr);
        } else if( singleLinkSelected() ) {
            clearAllActions(theMenuMgr);
//            // now add the actions
            ((RouterTypeMenuManager)linkTypeManager).setSingleLinkEdit(true);
            ((RouterTypeMenuManager)linkTypeManager).setInitialSelection();
            addDiagramActions(theMenuMgr);
        } else {
            addDiagramActions(theMenuMgr);
            
            if( doRemoveOpen() ) {
                // Remove Open
                IAction openAction = getAction(ModelerGlobalActions.OPEN);
                if (openAction != null && theMenuMgr.find(openAction.getId()) != null) {
                	theMenuMgr.remove(openAction.getId());
                }
            }
        }
    }
    
    private void addDiagramActions(IMenuManager theMenuMgr) {
        // now add the actions
        theMenuMgr.add(new Separator());
        theMenuMgr.add(new GroupMarker(D_MARKER));
        theMenuMgr.appendToGroup(D_MARKER, linkTypeManager);
        ((RouterTypeMenuManager)linkTypeManager).setInitialSelection();
        theMenuMgr.appendToGroup(D_MARKER, this.refreshDiagramAction);
        theMenuMgr.appendToGroup(D_MARKER, this.upPackageDiagramAction);
        theMenuMgr.appendToGroup(D_MARKER, this.saveDiagramAction);
        theMenuMgr.appendToGroup(D_MARKER, this.diagramPageSetupAction);
        theMenuMgr.appendToGroup(D_MARKER, this.showPageGridAction);
        
        theMenuMgr.add(new Separator());
    }
    
    private void removeDiagramActions(IMenuManager theMenuMgr) {
        if ( theMenuMgr.find(D_MARKER) != null )
            theMenuMgr.remove(D_MARKER);
        if ( theMenuMgr.find( this.refreshDiagramAction.getId() ) != null )
            theMenuMgr.remove( this.refreshDiagramAction.getId() );
        if ( theMenuMgr.find( this.upPackageDiagramAction.getId() ) != null )
            theMenuMgr.remove( upPackageDiagramAction.getId() );
        if ( theMenuMgr.find( this.linkTypeManager.getId() ) != null )
            theMenuMgr.remove( this.linkTypeManager.getId() );
        if ( theMenuMgr.find( this.saveDiagramAction.getId() ) != null )
            theMenuMgr.remove( this.saveDiagramAction.getId() );
        if ( theMenuMgr.find( this.diagramPageSetupAction.getId() ) != null )
            theMenuMgr.remove( this.diagramPageSetupAction.getId() );
        if ( theMenuMgr.find( this.showPageGridAction.getId() ) != null )
            theMenuMgr.remove( this.showPageGridAction.getId() );
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void contributeExportedActions(IMenuManager theMenuMgr) {
        // nothing to add to the main menu
//        if ((theMenuMgr.getId() != null) && theMenuMgr.getId().equals(ModelerActionBarIdManager.getEditMenuId())) {
//            // edit menu contributions should only happen one time (contributeToMenu(IMenuManager)). 
//            // call createActionContributionItem so that the contributions visibility can be controlled
//            // by the pageActivate/pageDeactivate methods.
//        } else {
//            // assume it must be a context menu. just add to end.
//        }
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

        linkTypeManager = null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#contributeToDiagramToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    @Override
    public void contributeToDiagramToolBar() {
        ToolBarManager tbm = ((DiagramEditor)getEditorPage()).getToolBarManager();

        if( tbm != null ) {
            tbm.removeAll();
            tbm.add(this.refreshDiagramAction);
            tbm.add(this.upPackageDiagramAction);
    		tbm.add(new Separator());
    		
    		IAction previewAction = ModelerSpecialActionManager.getAction(com.metamatrix.modeler.ui.UiConstants.Extensions.PREVIEW_DATA_ACTION_ID);
    		
            if( previewAction != null ) {
                tbm.add(previewAction);
                tbm.add(new Separator());
            }
//        tbm.add(new Separator());
//        tbm.add(newRectangleACI.getAction());
//        tbm.add(newEllipseACI.getAction());
//        tbm.add(newTextACI.getAction());
//        tbm.add(newNoteACI.getAction());
            tbm.add(new Separator());
            tbm.add(this.saveDiagramAction);
            tbm.add(this.diagramPageSetupAction);
            tbm.add(this.showPageGridAction);
            
            
            
            this.refreshDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
            this.upPackageDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
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
        boolean value = false;
        if( singleLinkSelected() ) {
            return true;
        } else if( !singleLinkSelected() ){
            // This is where we check for other cases.....
            value = false;
        }
        
        return value;
    }
}
