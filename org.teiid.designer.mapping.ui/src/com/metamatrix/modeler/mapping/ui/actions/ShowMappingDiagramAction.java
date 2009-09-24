/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

//import java.util.Iterator;
//import java.util.List;

import org.eclipse.core.resources.IFile;
//import org.eclipse.core.runtime.IStatus;
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
//import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.workspace.ModelResource;
//import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;

/**
 * ShowMappingDiagramAction
 */
public class ShowMappingDiagramAction extends MappingAction {
    
    //============================================================================================================================
    // Constants

    //============================================================================================================================
    // Constructors
    
    /**
     * Construct an instance of ShowMappingDiagramAction.
     * 
     */
    public ShowMappingDiagramAction() {
        super();
        this.setUseWaitCursor(false);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.SHOW_COARSE_MAPPING));
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
        Diagram detailedMappingDiagram = getDetailedMappingDiagram();
        if( detailedMappingDiagram != null ) {

            Diagram mappingDiagram = MappingDiagramUtil.getCoarseMappingDiagram(detailedMappingDiagram);
            if( mappingDiagram != null ) {
    
                ModelEditor editor = getModelEditorForObject(mappingDiagram, true);
                        
                if( editor != null ) {
                    editor.openModelObject(mappingDiagram);
                }
            }
        }
        setEnabled(false);
    }
    

    //============================================================================================================================
    // Declared Methods
    
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        // Don't care about selection here, so let's just check diagram type
        if( isDetailedMappingDiagram() )
            setEnabled(true);
        else
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
    
    private boolean isDetailedMappingDiagram() {
        ModelEditor editor = getActiveEditor();
        if( editor != null && editor.getCurrentPage() != null && editor.getCurrentPage() instanceof DiagramEditor ) {
            Diagram diagram = ((DiagramEditor)editor.getCurrentPage()).getDiagram();
            if( diagram != null ) {
                String diagramType = ((DiagramEditor)editor.getCurrentPage()).getDiagram().getType();
                if( diagramType != null && 
                    diagramType.equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID))
                    return true;
            }

        }
        return false;
    }
    
    
    private Diagram getDetailedMappingDiagram() {
        ModelEditor editor = getActiveEditor();
        if( editor != null && editor.getCurrentPage() != null && editor.getCurrentPage() instanceof DiagramEditor ) {
            Diagram diagram = ((DiagramEditor)editor.getCurrentPage()).getDiagram();
            if( diagram != null ) {
                String diagramType = ((DiagramEditor)editor.getCurrentPage()).getDiagram().getType();
                if( diagramType != null && 
                    diagramType.equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID))
                    return diagram;
            }

        }
        return null;
    }
}
