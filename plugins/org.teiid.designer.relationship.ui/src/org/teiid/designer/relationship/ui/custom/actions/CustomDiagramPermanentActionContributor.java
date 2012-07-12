/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.custom.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.relationship.RelationshipFolder;
import org.teiid.designer.relationship.ui.UiPlugin;
import org.teiid.designer.relationship.ui.actions.RelationshipAction;
import org.teiid.designer.relationship.ui.diagram.RelationshipDiagramUtil;
import org.teiid.designer.relationship.ui.navigation.actions.OpenInNavigatorAction;
import org.teiid.designer.ui.actions.IModelObjectActionContributor;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * PackageDiagramPermanentActionContributor
 */
public class CustomDiagramPermanentActionContributor implements IModelObjectActionContributor {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private RelationshipAction createDiagramAction;
    private RelationshipAction createDiagramSiblingAction;
    
    private OpenInNavigatorAction openInNavigatorAction;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public CustomDiagramPermanentActionContributor() {
        initActions();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.IModelObjectActionContributor#contributeToContextMenu(org.eclipse.jface.action.IMenuManager, org.eclipse.jface.viewers.ISelection)
     */
    public void contributeToContextMenu(IMenuManager theMenuMgr, ISelection theSelection) {
        // Need to check the selection first.
        
        if( SelectionUtilities.isSingleSelection(theSelection) ) {
            Object selectedObject = SelectionUtilities.getSelectedObject(theSelection);
            if( selectedObject instanceof EObject ) {
                EObject eObject = (EObject)selectedObject;
				if( RelationshipDiagramUtil.isRelationshipModelResource(eObject) ){
	                if( isPackage(eObject) ) {
	                    addToChildMenu(theMenuMgr);
	                }
	                if( eObject instanceof Diagram || (eObject.eContainer()!= null && 
	                    isPackage(eObject.eContainer()) )) {
	                    addToSiblingMenu(theMenuMgr);
	                } else if( eObject.eContainer() == null ) {
	                    addToSiblingMenu(theMenuMgr);
	                }
				}
            }else if( (selectedObject instanceof IResource) && 
						ModelUtilities.isModelFile((IResource)selectedObject) ) {
				// make sure this is a relationship model
			            	
				ModelResource mr = null;
			            	
				try {
					mr = ModelUtil.getModelResource((IFile)selectedObject, false);
				} catch (ModelWorkspaceException e) {
					e.printStackTrace();
				}
			            	
				if( mr != null && RelationshipDiagramUtil.isRelationshipModelResource(mr))
					addToChildMenu(theMenuMgr);
			}
        }
    }
    
    /**
     *  
     * @see org.teiid.designer.ui.actions.IModelObjectActionContributor#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions(ISelection theSelection) {
        List addedActions = new ArrayList();
        
        // Need to check the selection first.
        if( openInNavigatorAction.isEnabled() ) {
            if( SelectionUtilities.isSingleSelection(theSelection) &&
            	SelectionUtilities.isAllEObjects(theSelection)  ) {
                addedActions.add(openInNavigatorAction);
            }
        }
        
        return addedActions;
    }
    
    /**
     * Construct and register actions.
     */
    private void initActions() {
        createDiagramAction = new NewCustomDiagramAction();
        UiPlugin.registerActionForSelection(createDiagramAction);
        createDiagramSiblingAction = new NewCustomDiagramSiblingAction();
        UiPlugin.registerActionForSelection(createDiagramSiblingAction);
        openInNavigatorAction = new OpenInNavigatorAction();
        UiPlugin.registerActionForSelection(openInNavigatorAction);
    }
    
    private void addToChildMenu(IMenuManager theMenuMgr) {
//        String menuPath = ModelerActionBarIdManager.getInsertChildMenuId();
//                
//        IMenuManager mm = theMenuMgr.findMenuUsingPath(menuPath);
//                
//        if( mm != null ) {
//            mm.add(createDiagramAction);
//        } else {
//            theMenuMgr.add(createDiagramAction);
//        }
    }
    
    private void addToSiblingMenu(IMenuManager theMenuMgr) {
//        String menuPath = ModelerActionBarIdManager.getInsertSiblingMenuId();
//                
//        IMenuManager mm = theMenuMgr.findMenuUsingPath(menuPath);
//                
//        if( mm != null ) {
//            mm.add(createDiagramSiblingAction);
//        } else {
//            theMenuMgr.add(createDiagramSiblingAction);
//        }
    }
    
    private boolean isPackage(EObject eObject) {
        if( eObject instanceof RelationshipFolder )
            return true;
            
        return false;
    }
}

