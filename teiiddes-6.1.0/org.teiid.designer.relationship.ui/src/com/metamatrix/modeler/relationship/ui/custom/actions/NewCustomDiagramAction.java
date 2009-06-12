/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
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
public class NewCustomDiagramAction extends RelationshipAction implements UiConstants {
    
    //============================================================================================================================
    // Constants
    
    private static final boolean PERSIST_CUSTOM_DIAGRAMS = true;

    //============================================================================================================================
    // Constructors
    
    /**
     * Construct an instance of NewCustomDiagramAction.
     * 
     */
    public NewCustomDiagramAction() {
        super();
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CUSTOM_RELATIONSHIP_DIAGRAM_ICON));
		setText("Custom Relationship Diagram"); //$NON-NLS-1$
    }

    /**
     * Construct an instance of NewCustomDiagramAction.
     * @param theStyle
     */
    public NewCustomDiagramAction(int theStyle) {
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
        final Object selectedObject = SelectionUtilities.getSelectedObject(getSelection());
        if( (selectedObject instanceof IResource) && ModelUtilities.isModelFile((IResource)selectedObject) ) {
            ModelResource modelResource = null;
            
            try {
                modelResource = ModelUtilities.getModelResource((IFile)selectedObject, false);
            } catch (ModelWorkspaceException e) {
                String message = Util.getString("NewCustomDiagramAction.createCustomDiagramError", selectedObject.toString());  //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
            }
            if( modelResource != null ) {
                try {
                    Diagram result = modelResource.getModelDiagrams().createNewDiagram(null, PERSIST_CUSTOM_DIAGRAMS);
                    result.setType(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID);
                } catch (ModelWorkspaceException e) {
                    String message = Util.getString("NewCustomDiagramAction.createCustomDiagramError", modelResource.toString());  //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
                }
            }
        } else {
            final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
            if( eObject != null ) {
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                if( modelResource != null ) {
                    try {
                        Diagram result = modelResource.getModelDiagrams().createNewDiagram(eObject, PERSIST_CUSTOM_DIAGRAMS);
                        result.setType(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID);
                    } catch (ModelWorkspaceException e) {
                        String message = Util.getString("NewCustomDiagramAction.createCustomDiagramError", modelResource.toString());  //$NON-NLS-1$
                        Util.log(IStatus.ERROR, e, message);
                    }
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
        final Object selectedObject = SelectionUtilities.getSelectedObject(getSelection());
 		
        if( (selectedObject instanceof IResource) && ModelUtilities.isModelFile((IResource)selectedObject) ) {
            ModelResource modelResource = null;
            try {
            	
                modelResource = ModelUtilities.getModelResource((IFile)selectedObject, false);
            } catch (ModelWorkspaceException e) {
                String message = Util.getString("NewCustomDiagramAction.createCustomDiagramError", selectedObject.toString());  //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
            }
            if( modelResource != null && !modelResource.isReadOnly()) {
                    setEnabled(true);
                    return;
            }
        } else {
            final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
            
            if( eObject != null ) {
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                if( modelResource != null && !modelResource.isReadOnly() ) {
                    if( eObject instanceof RelationshipFolder ) {
                		setEnabled(true);
                		return;
                    }
                }
            }
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
			final Object selectedObject = SelectionUtilities.getSelectedObject(getSelection());
			ModelResource modelResource = null;
			 		
			if( (selectedObject instanceof IResource) && ModelUtilities.isModelFile((IResource)selectedObject) ) {

				try {
					modelResource = ModelUtilities.getModelResource((IFile)selectedObject, false);
				} catch (ModelWorkspaceException e) {
					String message = Util.getString("NewCustomDiagramAction.createCustomDiagramError", selectedObject.toString());  //$NON-NLS-1$
						Util.log(IStatus.ERROR, e, message);
				}
			} else {
				final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
            
				if( eObject != null ) {
					modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
				}
			}

			if( modelResource != null ) {
				ModelEditorManager.activate(modelResource, true);
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
