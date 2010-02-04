/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;


/** 
 * @since 4.2
 */
public class DiagramEditorUtil {
    
    //
    // Class constants:
    //
    private static final DiagramEditor[] EMPTY_DIAGRAM_EDITOR_ARRAY = {};

    //
    // Utility methods:
    //
    public static DiagramModelNode getCurrentRootModelNode() {
        DiagramEditor de = getVisibleDiagramEditor();
        if( de != null ) {
            return de.getCurrentModel();
        }
        
        return null;
    }
   
    public static Diagram getCurrentVisibleDiagram() {
        DiagramEditor de = getVisibleDiagramEditor();
        if( de != null  && de.getCurrentModel() != null ) {
            return (Diagram)de.getCurrentModel().getModelObject();
        }
        
        return null;
    }
    
    public static EObject getCurrentVisibleDiagramTarget() {
        Diagram diagram = getCurrentVisibleDiagram();
        if( diagram != null )
            return diagram.getTarget();
        
        return null;
    }
    
    public static void fireDiagramPropertyChange(String propId) {
        DiagramModelNode dmn = getCurrentRootModelNode();
        if( dmn != null ) {
            dmn.update(propId);
        }
    }

    public static DiagramEditor[] getInitializedDiagramEditors() {
        List rv = new ArrayList();

        Collection resources = ModelEditorManager.getOpenResources();
        Iterator itor = resources.iterator();
        while (itor.hasNext()) {
            IFile res = (IFile) itor.next();
            ModelEditor me = ModelEditorManager.getModelEditorForFile(res, false);
            if (me != null) {
                List editors = me.getAllEditors();
                for (int i = 0; i < editors.size(); i++) {
                    IEditorPart editor = (IEditorPart) editors.get(i);
                    if (editor instanceof DiagramEditor
                     && editor.getEditorSite() != null) {
                        rv.add(editor);
                    } // endif
                } // endfor
            } // endif -- me not null
        } // endwhile -- all editors
        
        return (DiagramEditor[]) rv.toArray(EMPTY_DIAGRAM_EDITOR_ARRAY);
    }

    public static DiagramEditor getVisibleDiagramEditor() {
        IWorkbenchPage page = DiagramUiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
        if ( page != null ) {
            // look through the open editors and see if there is one available for this model file.
            IEditorPart activeEditor = page.getActiveEditor();
            if( activeEditor instanceof ModelEditor ) {
                IEditorPart editor = ((ModelEditor)activeEditor).getCurrentPage();
                if ( editor != null && editor instanceof DiagramEditor ) {
                    return (DiagramEditor)editor;
                }
            }
    
        }
        return null;
    }
    
    public static DiagramEditor getDiagramEditor(Diagram diagram) {
        IWorkbenchPage page = DiagramUiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
        if ( page != null ) {
            // look through the open editors and see if there is one available for this model file.
            IEditorReference[] activeEditorReferences = page.getEditorReferences();
            for( int i=0; i<activeEditorReferences.length; i++ ) {
                IEditorPart theEditor = activeEditorReferences[i].getEditor(false);
                if( theEditor != null && theEditor instanceof ModelEditor ) {
                    IEditorPart editor = ((ModelEditor)theEditor).getCurrentPage();
                    if ( editor != null && editor instanceof DiagramEditor ) {
                        Diagram editorDiagram = ((DiagramEditor)editor).getDiagram();
                        if( editorDiagram != null && diagram == editorDiagram ) {
                            return (DiagramEditor)editor;
                        }
                    }
                }
            }
    
        }
        return null;
    }

    public static double getCurrentZoomFactor() {
        DiagramEditor de = getVisibleDiagramEditor();
        if( de != null ) {
            return de.getCurrentZoomFactor();
        }
        
        return 1.0;
    }

    public static NodeConnectionEditPart getSelectedLink() {
        DiagramEditor de = getVisibleDiagramEditor();
        if( de != null ) {
            List selectedEPs = de.getDiagramViewer().getSelectedEditParts();
            if( ! selectedEPs.isEmpty() && selectedEPs.size() == 1) {
                Object selectedEP = selectedEPs.get(0);
                if( selectedEP instanceof NodeConnectionEditPart )
                    return (NodeConnectionEditPart)selectedEP;
            }
        }
        
        return null;
    }
    
    public static int getCurrentDiagramRouterStyle() {
        int iRouter = DiagramUiUtilities.getCurrentRouterStyle();
        DiagramModelNode dmn = getCurrentRootModelNode();
        if( dmn != null ) {
            Diagram diagram = (Diagram)dmn.getModelObject();
            if( diagram != null) {
                DiagramLinkType dlt = diagram.getLinkType();
                if( dlt != null )
                    iRouter = dlt.getValue();
                else
                    iRouter = DiagramUiUtilities.getCurrentRouterStyle();
            }
        }
        return iRouter;
    }
    
    public static List getCurrentDiagramNodeEObjects() {
        List contents = new ArrayList();
        
        DiagramEditor de = getVisibleDiagramEditor();
        if( de != null ) {
            List children = de.getCurrentModel().getChildren();
            Iterator iter = children.iterator();
            Object nextObj = null;
            while( iter.hasNext() ) {
                nextObj = iter.next();
                if( nextObj instanceof DiagramModelNode &&
                    !(nextObj instanceof NodeConnectionModel) ) {
                    contents.add(((DiagramModelNode)nextObj).getModelObject());
                }
            }
        }
        if( contents.isEmpty() )
            return Collections.EMPTY_LIST;
        
        return contents;
    }
    
    
    public static boolean setCurrentDiagramRouterStyle(int iRouter) {
        boolean succeeded = false;
        int currentRouter = getCurrentDiagramRouterStyle();
        if( iRouter != currentRouter) {
            DiagramModelNode dmn = getCurrentRootModelNode();
            if( dmn != null ) {
                Diagram diagram = (Diagram)dmn.getModelObject();
                if( diagram != null) {
                    boolean requiredStart = ModelerCore.startTxn(true, true, "Set Diagram Link Type", diagram); //$NON-NLS-1$
                    succeeded = false;
                    try {
                        DiagramLinkType theType = DiagramLinkType.get(iRouter);
                        diagram.setLinkType(theType);
                        succeeded = true;
                    } finally {
                        if (requiredStart) {
                            if ( succeeded ) {
                                ModelerCore.commitTxn( );
                            } else {
                                ModelerCore.rollbackTxn( );
                            }
                        }
                    }
                }
            }
        }
        return succeeded;
    }
    
    
    public static DiagramDecoratorHandler getDecoratorHandler() {
        DiagramEditor de = getVisibleDiagramEditor();
        if( de != null ) {
            return de.getDecoratorHandler();
        }
        
        return null;
    }
    
    
    public static int getErrorState(EObject eObj) {
        int errorState = DiagramUiConstants.NO_ERRORS;
        DiagramDecoratorHandler dh = getDecoratorHandler();
        if( dh != null ) {
            errorState = dh.getErrorState(eObj);
        }
        
        return errorState;
    }
    
    public static boolean isDiagramUnderConstruction(Diagram diagram) {
        DiagramEditor de = getDiagramEditor(diagram);
        if( de != null ) {
            DiagramEditPart diagramEP = (DiagramEditPart)de.getDiagramViewer().getContents();
            if( diagramEP != null ) {
                return diagramEP.isUnderConstruction();
            }
        }
        
        return false;
    }
    
    public static boolean setDiagramUnderConstruction(Diagram diagram) {
        DiagramEditor de = getDiagramEditor(diagram);
        if( de != null ) {
            DiagramEditPart diagramEP = (DiagramEditPart)de.getDiagramViewer().getContents();
            if( diagramEP != null ) {
                diagramEP.setUnderConstruction(true);
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean setDiagramConstructionComplete(Diagram diagram, boolean updateLinkedParts) {
        DiagramEditor de = getDiagramEditor(diagram);
        if( de != null ) {
            DiagramEditPart diagramEP = (DiagramEditPart)de.getDiagramViewer().getContents();
            if( diagramEP != null ) {
                diagramEP.constructionCompleted(updateLinkedParts);
                return true;
            }
        }
        
        return false;
    }
}
