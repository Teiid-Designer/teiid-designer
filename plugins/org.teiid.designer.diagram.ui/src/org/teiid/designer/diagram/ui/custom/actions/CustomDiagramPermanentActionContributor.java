/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.custom.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.actions.DiagramAction;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.actions.IModelObjectActionContributor;
import org.teiid.designer.ui.actions.ModelerActionBarIdManager;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * PackageDiagramPermanentActionContributor
 */
public class CustomDiagramPermanentActionContributor implements IModelObjectActionContributor {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private DiagramAction createDiagramAction;
    private DiagramAction createDiagramSiblingAction;
    private DiagramAction addToNewCustomDiagramAction;
    
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
            
            if( supportsDiagrams(selectedObject) ) {
                if( selectedObject instanceof EObject ) {
                    EObject eObject = (EObject)selectedObject;
                    
                    if( isPackage(eObject))
                        addToChildMenu(theMenuMgr);

                    if( eObject instanceof Diagram || (eObject.eContainer() != null && isPackage(eObject.eContainer())) ) {
                        addToSiblingMenu(theMenuMgr);
                    } else if( eObject.eContainer() == null ) {
                        addToSiblingMenu(theMenuMgr);
                    }
                } else if( (selectedObject instanceof IResource) && 
                            ModelUtilities.isModelFile((IResource)selectedObject)&&
                            !ModelUtil.isXsdFile((IResource)selectedObject) && !ModelIdentifier.isRelationshipModel((IResource)selectedObject) ) {
                    addToResourceChildMenu(theMenuMgr);
                }
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
        if( addToNewCustomDiagramAction.isEnabled() ) {
            if( SelectionUtilities.isSingleSelection(theSelection)  ) {
                Object selectedObject = SelectionUtilities.getSelectedObject(theSelection);
                
                if( supportsDiagrams(selectedObject) ) {
                    if( selectedObject instanceof EObject ) {
                        addedActions.add(addToNewCustomDiagramAction);
                    }
                }
            }  else if( !SelectionUtilities.getSelectedEObjects(theSelection).isEmpty() ) {
                addedActions.add(addToNewCustomDiagramAction);
            }
        }
        
        return addedActions;
    }
    
    /**
     * Construct and register actions.
     */
    private void initActions() {
        createDiagramAction = new NewCustomDiagramAction();
        DiagramUiPlugin.registerDiagramActionForSelection(createDiagramAction);
        createDiagramSiblingAction = new NewCustomDiagramSiblingAction();
        DiagramUiPlugin.registerDiagramActionForSelection(createDiagramSiblingAction);
        addToNewCustomDiagramAction = new AddToNewCustomDiagramAction();
        DiagramUiPlugin.registerDiagramActionForSelection(addToNewCustomDiagramAction);
    }
    
    private void addToChildMenu(IMenuManager theMenuMgr) {
        String menuPath = ModelerActionBarIdManager.getInsertChildMenuId();
                
        IMenuManager mm = theMenuMgr.findMenuUsingPath(menuPath);
                
        if( mm != null ) {
        	mm.add(new Separator());
            mm.add(createDiagramAction);
        }
    }
    
    private void addToResourceChildMenu(IMenuManager theMenuMgr) {
        String menuPath = ModelerActionBarIdManager.getInsertChildMenuId();
        
        IMenuManager mm = theMenuMgr.findMenuUsingPath(menuPath);
                
        if( mm != null ) {
        	mm.add(new Separator());
            mm.add(createDiagramAction);
        } else {
        	theMenuMgr.add(new Separator());
            theMenuMgr.add(createDiagramAction);
        }
    }
    
    private void addToSiblingMenu(IMenuManager theMenuMgr) {
        String menuPath = ModelerActionBarIdManager.getInsertSiblingMenuId();
                
        IMenuManager mm = theMenuMgr.findMenuUsingPath(menuPath);
                
        if( mm != null ) {
        	mm.add(new Separator());
            mm.add(createDiagramSiblingAction);
        }
    }
    
    private boolean isPackage(EObject eObject) {
        MetamodelAspect aspect = ModelObjectUtilities.getUmlAspect(eObject);

        if( aspect instanceof UmlPackage )
            return true;
            
        return false;
    }
    
    private boolean supportsDiagrams(Object input) {
    	ModelResource mr = null;
    	
    	if( input instanceof EObject ) {
    		mr = ModelUtilities.getModelResourceForModelObject((EObject)input);
    	} else if( input instanceof IFile ) {
    		mr = ModelUtilities.getModelResourceForIFile((IFile)input, false);
    	}
    	if( mr == null ) {
    		return false;
    	}
        if( input instanceof Diagram ) {
            // Let's get the model resource and call with it instead of diagram.
        	if( !ModelIdentifier.isRelationshipModel(mr) ) {
        		return ModelUtilities.supportsDiagrams(mr);
        	}
        	
            return false;
        }
        if( ModelIdentifier.isRelationshipModel(mr) ) {
        	return false;
        }
        
        return ModelUtilities.supportsDiagrams(input);
    }
    
}

