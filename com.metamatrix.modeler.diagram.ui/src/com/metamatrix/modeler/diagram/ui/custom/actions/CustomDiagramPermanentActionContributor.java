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

package com.metamatrix.modeler.diagram.ui.custom.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.DiagramAction;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.IModelObjectActionContributor;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

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
     * @see com.metamatrix.modeler.ui.actions.IModelObjectActionContributor#contributeToContextMenu(org.eclipse.jface.action.IMenuManager, org.eclipse.jface.viewers.ISelection)
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
                            !ModelUtil.isXsdFile((IResource)selectedObject) ) {
                    addToResourceChildMenu(theMenuMgr);
                }
            }
        } 
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectActionContributor#contributeToEditMenu(org.eclipse.jface.action.IMenuManager, org.eclipse.jface.viewers.ISelection)
     */
    public void contributeToEditMenu(IMenuManager theMenuMgr, ISelection theSelection) {
        // Need to check the selection first.
        
        if( SelectionUtilities.isSingleSelection(theSelection) ) {
            Object selectedObject = SelectionUtilities.getSelectedObject(theSelection);
            
            if( supportsDiagrams(selectedObject) ) {
                if( selectedObject instanceof EObject ) {
                    EObject eObject = (EObject)selectedObject;
                    
                    if( isPackage(eObject) ) {
                        addToChildMenu(theMenuMgr);
                    }
                    
                    if( eObject instanceof Diagram || (eObject.eContainer() != null && isPackage(eObject.eContainer())) ) {
                        addToSiblingMenu(theMenuMgr);
                    } else if( eObject.eContainer() == null ) {
                        addToSiblingMenu(theMenuMgr);
                    }

                } else if( (selectedObject instanceof IResource) && 
                            ModelUtilities.isModelFile((IResource)selectedObject) &&
                            !ModelUtil.isXsdFile((IResource)selectedObject)) {
                        addToResourceChildMenu(theMenuMgr);
                }
            }
        }  
    }

    /**
     *  
     * @see com.metamatrix.modeler.ui.actions.IModelObjectActionContributor#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
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
            mm.add(createDiagramAction);
        }
    }
    
    private void addToResourceChildMenu(IMenuManager theMenuMgr) {
        String menuPath = ModelerActionBarIdManager.getInsertChildMenuId();
        
        IMenuManager mm = theMenuMgr.findMenuUsingPath(menuPath);
                
        if( mm != null ) {
            mm.add(createDiagramAction);
        } else {
            theMenuMgr.add(createDiagramAction);
        }
    }
    
    private void addToSiblingMenu(IMenuManager theMenuMgr) {
        String menuPath = ModelerActionBarIdManager.getInsertSiblingMenuId();
                
        IMenuManager mm = theMenuMgr.findMenuUsingPath(menuPath);
                
        if( mm != null ) {
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
        if( input instanceof Diagram ) {
            // Let's get the model resource and call with it instead of diagram.
            return ModelUtilities.supportsDiagrams(ModelUtilities.getModelResourceForModelObject((EObject)input));
        }
        
        return ModelUtilities.supportsDiagrams(input);
    }
    
}

