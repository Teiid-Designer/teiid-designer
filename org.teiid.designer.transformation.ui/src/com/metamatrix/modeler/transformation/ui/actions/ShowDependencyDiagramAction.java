/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.Iterator;
import java.util.List;
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
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelTransformations;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ShowDependencyDiagramAction
 */
public class ShowDependencyDiagramAction extends TransformationAction implements UiConstants{
    
    //============================================================================================================================
    // Constants

    //============================================================================================================================
    // Constructors
    
    /**
     * Construct an instance of ShowDependencyDiagramAction.
     * 
     */
    public ShowDependencyDiagramAction() {
        super(null);
        this.setUseWaitCursor(false);
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
            if( modelResource != null && ModelUtilities.isVirtual(modelResource)) {
                try {

                    List transformations = modelResource.getModelTransformations().getTransformations(eObject);
                    if( transformations != null && transformations.size() == 1 ) {
						ModelEditor editor = getModelEditorForObject(eObject, true);      
                        
                        if( editor != null ) {
                        	ModelEditorManager.activate(editor);
                            Diagram depDiagram = getDependencyDiagram(modelResource, eObject);
                            if( depDiagram != null ) {
                                editor.closeObjectEditor();
                            	editor.openModelObject(depDiagram);
                            }
                        }

                    }
                } catch (ModelWorkspaceException e) {
                    String message = Util.getString("ShowDependencyDiagramAction.showDependencyDiagramError", modelResource.toString());  //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
                }
            }
        }
        determineEnablement();
    }
    
    private Diagram getDependencyDiagram(ModelResource modelResource, EObject eObject) {
        Iterator iter = null;
        
        try {
            iter = modelResource.getModelDiagrams().getDiagrams(eObject).iterator();
        } catch (ModelWorkspaceException e) {
            String message = Util.getString("ShowDependencyDiagramAction.getDiagramsError", modelResource.toString());  //$NON-NLS-1$
            Util.log(IStatus.ERROR, e, message);
        }
        if( iter != null ) {
            Diagram nextDiagram = null;
            while( iter.hasNext() ) {
                nextDiagram = (Diagram)iter.next();
                if( nextDiagram.getType() != null &&
                    nextDiagram.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID))
                    return nextDiagram;
            }
        }
        Diagram depDiagram = null;
        // Couldn't find one so create one
        boolean requiredStart = ModelerCore.startTxn(false, true, "Create Dependency Diagram", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            depDiagram = modelResource.getModelDiagrams().createNewDiagram(eObject, false); // Do Not persist this diagram.
            depDiagram.setType(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID);
            succeeded = true;
        } catch (ModelWorkspaceException mwe) {
            String message = Util.getString("ShowDependencyDiagramAction.createDependencyDiagramError", modelResource.toString());  //$NON-NLS-1$
            Util.log(IStatus.ERROR, mwe, message);
        } finally {
            if( requiredStart ) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return depDiagram;
    }

    //============================================================================================================================
    // Declared Methods
    
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
        if( eObject != null && canLegallyEditResource() ) {
            // See if it has a transformation object:
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
            if( modelResource != null && ModelUtilities.isVirtual(modelResource)) {
                try {
                    ModelTransformations modelTransformations = modelResource.getModelTransformations();
                    if ( modelTransformations != null ) { 
                        List transformations = modelTransformations.getTransformations(eObject);
                        if( transformations != null && transformations.size() == 1 ) {
                            setEnabled(true);
                            return;
                        }
                    }
                } catch (ModelWorkspaceException e) {
                    String message = Util.getString("ShowDependencyDiagramAction.showDependencyDiagramError", modelResource.toString());  //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
                }
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
