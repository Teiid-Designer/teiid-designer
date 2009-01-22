/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ShowDetailedMappingDiagramAction
 */
public class ShowDetailedMappingDiagramAction extends MappingAction {
    
    //============================================================================================================================
    // Constants

    //============================================================================================================================
    // Constructors
    
    /**
     * Construct an instance of ShowMappingDiagramAction.
     * 
     */
    public ShowDetailedMappingDiagramAction() {
        super();
        this.setUseWaitCursor(false);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.SHOW_DETAILED_MAPPING));

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
        final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
        if( eObject != null ) {
            // See if it has a transformation object:
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
            if( modelResource != null && wasMappingClassSelected(eObject) ) {

                // Let's get Transformation Diagram (i.e. create it if it doesn't exist yet)
                Diagram transformationDiagram = getTransformationDiagram(modelResource, eObject);
                ModelEditor editor = getModelEditorForObject(transformationDiagram, true);
                
                if( editor != null ) {
                    editor.openModelObject(transformationDiagram);
                }
            }
        }
        determineEnablement();
    }
    
    private boolean wasMappingClassSelected(EObject eObject) {
    	if( ModelUtilities.getModelResourceForModelObject(eObject) != null )
        	return (eObject instanceof MappingClass) && TransformationHelper.isVirtualSqlTable(eObject);
  		return false;
    }
    
    private Diagram getTransformationDiagram(ModelResource modelResource, EObject eObject) {
        Iterator iter = null;
        String diagramType = null;
        if( eObject instanceof StagingTable ) {
            diagramType = com.metamatrix.modeler.transformation.ui.PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID;
        } else {
            diagramType = PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID;
        }
        try {
            iter = modelResource.getModelDiagrams().getDiagrams(eObject).iterator();
        } catch (ModelWorkspaceException e) {
            String message = UiConstants.Util.getString("ShowDetailedMappingDiagram.getDiagramsError", modelResource.toString());  //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        }
        if( iter != null ) {
            Diagram nextDiagram = null;
            while( iter.hasNext() ) {
                nextDiagram = (Diagram)iter.next();
                if( nextDiagram.getType() != null &&
                    nextDiagram.getType().equals(diagramType))
                    return nextDiagram;
            }
        }
        // Couldn't find one so create one
        boolean requiresStart = false;
        boolean succeeded = false;
        try {
            requiresStart = ModelerCore.startTxn(false, true, "Create Transformation Diagram", this); //$NON-NLS-1$
            
            Diagram depDiagram = modelResource.getModelDiagrams().createNewDiagram(eObject, true); // Do Not persist this diagram.
            depDiagram.setType(diagramType);
            succeeded = true;
            return depDiagram;
        } catch (ModelWorkspaceException mwe) {
            String message = UiConstants.Util.getString("ShowDetailedMappingDiagram.createMappingDiagramError", modelResource.toString());  //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, mwe, message);
        } finally {
            if( requiresStart ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
        
        return null;
    }

    //============================================================================================================================
    // Declared Methods
    
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
        if( eObject != null && wasMappingClassSelected(eObject) ) {
            // See if it has a transformation object:
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
            if( modelResource != null && ModelUtilities.isVirtual(modelResource)) {
              setEnabled(true);
              return;
            }
        }

        setEnabled(false);
    }
    
    private static ModelEditor getModelEditorForObject(EObject object, boolean forceOpen) {
        ModelEditor result = null;
        
        IFile file = null; 
        ModelResource mdlRsrc = ModelUtilities.getModelResourceForModelObject(object);
        if ( mdlRsrc != null){
            file = (IFile) mdlRsrc.getResource();
            result = getModelEditorForFile(file, forceOpen);
        }
        return result;
    }
    
    // =============================================
    // Private Methods

    private static ModelEditor getModelEditorForFile(IFile file, boolean forceOpen) {
        ModelEditor result = null;
        if ( file != null ) {
            IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
            if ( page != null ) {
                // look through the open editors and see if there is one available for this model file.
                IEditorReference[] editors = page.getEditorReferences();
                for ( int i=0 ; i<editors.length ; ++i ) {
                    IEditorPart editor = editors[i].getEditor(false);
                    if ( editor != null ) {
                        IEditorInput input = editor.getEditorInput();
                        if ( input instanceof IFileEditorInput ) {
                            if ( file.equals(((IFileEditorInput) input).getFile()) ) {
                                // found it;
                                if ( editor instanceof ModelEditor ) {
                                    result = (ModelEditor) editor;
                                }
                                break;
                            }
                        }
                    }
                }
            
                if ( result == null && forceOpen) {

                    // there is no editor open for this object.  Open one and hand it the double-click target.
                    try {
						IEditorPart editor = IDE.openEditor(page, file);
                        if ( editor instanceof ModelEditor ) {
                            result = (ModelEditor) editor;
                        }
    
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}
