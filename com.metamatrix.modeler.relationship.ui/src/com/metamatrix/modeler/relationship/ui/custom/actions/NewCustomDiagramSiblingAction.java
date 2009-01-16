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

package com.metamatrix.modeler.relationship.ui.custom.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.actions.RelationshipAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * NewCustomDiagramAction
 */
public class NewCustomDiagramSiblingAction extends RelationshipAction implements UiConstants {
    
    //============================================================================================================================
    // Constants
    
    private static final boolean PERSIST_CUSTOM_DIAGRAMS = true;

    //============================================================================================================================
    // Constructors
    
    /**
     * Construct an instance of NewCustomDiagramAction.
     * 
     */
    public NewCustomDiagramSiblingAction() {
        super();
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CUSTOM_RELATIONSHIP_DIAGRAM_ICON));
		setText("Custom Relationship Diagram"); //$NON-NLS-1$
    }

    /**
     * Construct an instance of NewCustomDiagramAction.
     * @param theStyle
     */
    public NewCustomDiagramSiblingAction(int theStyle) {
        super(theStyle);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CUSTOM_RELATIONSHIP_DIAGRAM_ICON));
		setText("Custom Relationship Diagram"); //$NON-NLS-1$
    }


    
    //============================================================================================================================
    // ISelectionListener Methods
    
    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        super.selectionChanged(part, selection);
        determineEnablement();
    }

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        final EObject selectedEObject = SelectionUtilities.getSelectedEObject(getSelection());
        
        if( selectedEObject != null ) {
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(selectedEObject);
            
            if( modelResource != null ) {
                EObject eObject = selectedEObject.eContainer();
                // If eObject == null, then selected object is a package/schema, which needs a null eObject as the
                // target, so we don't have to check for null here.
                try {
                    Diagram result = modelResource.getModelDiagrams().createNewDiagram(eObject, PERSIST_CUSTOM_DIAGRAMS);
                    result.setType(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID);
                } catch (ModelWorkspaceException e) {
                    String message = Util.getString("NewCustomDiagramSiblingAction.createCustomDiagramError", modelResource.toString());  //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
                }
            }
        }

        determineEnablement();
    }

    //============================================================================================================================
    // Declared Methods
    
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        final EObject selectedEObject = SelectionUtilities.getSelectedEObject(getSelection());
        if( selectedEObject != null ) {
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(selectedEObject);
            // Let's get it's parent
//            EObject eObject = selectedEObject.eContainer();
//            if( eObject != null ) {
//                if( modelResource != null && !modelResource.isReadOnly()) {
//                    if( eObject instanceof RelationshipFolder ) {
//                        setEnabled(true);
//                        return;
//                    }
//                }
//            } else {
                if( modelResource != null &&! modelResource.isReadOnly() ) {
                    setEnabled(true);
                    return;
                }
//            }
        }
        setEnabled(false);
    }
    
	/**
	 * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
	 * the run at the last minute.
	 * This overrides the AbstractAction preRun() method.
	 */
	@Override
    protected boolean preRun() {
		if( requiresEditorForRun() ) {
			final EObject selectedEObject = SelectionUtilities.getSelectedEObject(getSelection());
        
			if( selectedEObject != null ) {
				ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(selectedEObject);
				if( modelResource != null ) {
					ModelEditorManager.activate(modelResource, true);
				}
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
	 */
	@Override
    protected boolean requiresEditorForRun() {
		return true;
	}
}

