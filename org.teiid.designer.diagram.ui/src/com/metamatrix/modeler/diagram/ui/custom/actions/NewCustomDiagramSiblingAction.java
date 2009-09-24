/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
//import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
//import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.DiagramAction;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
//import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * NewCustomDiagramAction
 */
public class NewCustomDiagramSiblingAction extends DiagramAction implements DiagramUiConstants {
    
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
    }

    /**
     * Construct an instance of NewCustomDiagramAction.
     * @param theStyle
     */
    public NewCustomDiagramSiblingAction(int theStyle) {
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
        final EObject selectedEObject = SelectionUtilities.getSelectedEObject(getSelection());
        
        boolean requiredStart = false;
        boolean succeeded = false;
        try {
            //------------------------------------------------- 
            // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
            // will result in only one transaction?
            //------------------------------------------------- 

            requiredStart = ModelerCore.startTxn(true, false, "New Custom Diagram", this); //$NON-NLS-1$$
            
            createDiagram(selectedEObject);
            
            succeeded = true;
            
        } catch (ModelWorkspaceException ex){
            String message = Util.getString("NewCustomDiagramSiblingAction.createCustomDiagramError", selectedEObject.toString());  //$NON-NLS-1$
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
    
    private void createDiagram(EObject selectedEObject) throws ModelWorkspaceException {
        if( selectedEObject != null ) {
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(selectedEObject);
            
            if( modelResource != null ) {
                EObject eObject = null;
                if( !(selectedEObject instanceof Diagram) )
                    eObject = selectedEObject.eContainer();
                else {
                    //We have a diagram, if target == ModelAnnotation, keep eObject = null
                    // else set eObject to same target
                    EObject target = ((Diagram)selectedEObject).getTarget();
                    if( !(target instanceof ModelAnnotation) )
                        eObject = target;
                        
                }
                // If eObject == null, then selected object is a package/schema, which needs a null eObject as the
                // target, so we don't have to check for null here.
                
                Diagram result = modelResource.getModelDiagrams().createNewDiagram(eObject, PERSIST_CUSTOM_DIAGRAMS);
                result.setType(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID);
            }
        }
    }
    
    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
//    protected void doRun() {
//        final EObject selectedEObject = SelectionUtilities.getSelectedEObject(getSelection());
//        
//        if( selectedEObject != null ) {
//            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(selectedEObject);
//            
//            if( modelResource != null ) {
//                EObject eObject = null;
//                if( !(selectedEObject instanceof Diagram) )
//                    eObject = selectedEObject.eContainer();
//                else {
//                    //We have a diagram, if target == ModelAnnotation, keep eObject = null
//                    // else set eObject to same target
//                    EObject target = ((Diagram)selectedEObject).getTarget();
//                    if( !(target instanceof ModelAnnotation) )
//                        eObject = target;
//                        
//                }
//                // If eObject == null, then selected object is a package/schema, which needs a null eObject as the
//                // target, so we don't have to check for null here.
//                try {
//                    Diagram result = modelResource.getModelDiagrams().createNewDiagram(eObject, PERSIST_CUSTOM_DIAGRAMS);
//                    result.setType(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID);
//                } catch (ModelWorkspaceException e) {
//                    String message = Util.getString("NewCustomDiagramSiblingAction.createCustomDiagramError", modelResource.toString());  //$NON-NLS-1$
//                    Util.log(IStatus.ERROR, e, message);
//                }
//            }
//        }
//
//        determineEnablement();
//    }

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
//                    MetamodelAspect aspect = ModelObjectUtilities.getUmlAspect(eObject);
//        
//                    if ( aspect != null && aspect instanceof UmlPackage ) {
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

