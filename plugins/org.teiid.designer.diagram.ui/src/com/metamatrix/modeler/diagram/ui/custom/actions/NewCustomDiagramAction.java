/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.diagram.ui.actions.DiagramAction;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * NewCustomDiagramAction
 */
public class NewCustomDiagramAction extends DiagramAction implements DiagramUiConstants {
    
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
    }

    /**
     * Construct an instance of NewCustomDiagramAction.
     * @param theStyle
     */
    public NewCustomDiagramAction(int theStyle) {
        super(theStyle);
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
        
        boolean requiredStart = false;
        boolean succeeded = false;
        try {
            //------------------------------------------------- 
            // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
            // will result in only one transaction?
            //------------------------------------------------- 

            requiredStart = ModelerCore.startTxn(true, false, "New Custom Diagram", this); //$NON-NLS-1$$
            
            createDiagram(selectedObject);
            
            succeeded = true;
            
        } catch (ModelWorkspaceException ex){
            String message = Util.getString("NewCustomDiagramAction.createCustomDiagramError", selectedObject.toString());  //$NON-NLS-1$
            Util.log(IStatus.ERROR, ex, message);
        } finally {
            if(requiredStart){
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }


        determineEnablement();
    }
    
    private void createDiagram(Object selectedObject) throws ModelWorkspaceException {
        if( (selectedObject instanceof IResource) && ModelUtilities.isModelFile((IResource)selectedObject) ) {
            ModelResource modelResource = ModelUtil.getModelResource((IFile)selectedObject, false);

            if( modelResource != null ) {
                Diagram result = modelResource.getModelDiagrams().createNewDiagram(null, PERSIST_CUSTOM_DIAGRAMS);
                result.setType(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID);
            }
        } else {
            final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
            if( eObject != null ) {
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                if( modelResource != null ) {
                    Diagram result = modelResource.getModelDiagrams().createNewDiagram(eObject, PERSIST_CUSTOM_DIAGRAMS);
                    result.setType(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID);
                }
            }
        }
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
            	
                modelResource = ModelUtil.getModelResource((IFile)selectedObject, false);
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
                    MetamodelAspect aspect = ModelObjectUtilities.getUmlAspect(eObject);
        
                    if ( aspect != null && aspect instanceof UmlPackage ) {
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
					modelResource = ModelUtil.getModelResource((IFile)selectedObject, false);
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
