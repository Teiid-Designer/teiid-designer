/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.pakkage.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.PluginConstants;
import org.teiid.designer.diagram.ui.actions.DiagramEditorAction;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * ShowParentDiagramAction
 */
public class ShowParentDiagramAction extends DiagramEditorAction {

    /**
     * Construct an instance of ShowParentDiagramAction.
     * 
     */
    public ShowParentDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.UP_PACKAGE_DIAGRAM));
        setEnabled(false);
    }

    /* (non-Javadoc)
     * see org.teiid.designer.ui.common.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        if( editor != null ) {
            Diagram diagram = getParentPackageDiagram();
            if( diagram != null ) {
                // Mark current navigation location using current open object
                UiUtil.getWorkbenchPage().getNavigationHistory().markLocation(editor);
                editor.openContext(diagram);
            }
        }
        determineEnablement();
    }
    
    public void determineEnablement() {
        if( editor != null ) {
            Diagram diagram = getParentPackageDiagram();
            if( diagram != null )
                setEnabled(true);
            else
                setEnabled(false);
        }

    }
    
    private Diagram getParentPackageDiagram() {
        Diagram parentPackageDiagram = null;
        DiagramModelNode diagramNode = editor.getCurrentModel();
        if( diagramNode != null ) {
            Diagram currentDiagram = (Diagram)diagramNode.getModelObject();
            if( currentDiagram != null &&
                currentDiagram.getType() != null && 
                currentDiagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID) ) {
                Object diagramTarget = currentDiagram.getTarget();
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(currentDiagram);
                if( diagramTarget != null && 
                    diagramTarget instanceof EObject && 
                    modelResource != null &&
                    !(diagramTarget instanceof ModelAnnotation)) {
                    EObject packageObject = (EObject)diagramTarget;
                    Object parentObject = packageObject.eContainer();
                    if( parentObject != null && parentObject instanceof EObject ) {
                        try {
                            // get diagrams and find package diagram.
                            List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams((EObject)parentObject));
                            Iterator iter = diagramList.iterator();
                            Diagram nextDiagram = null;
                            while( iter.hasNext() && parentPackageDiagram == null ) {
                                nextDiagram = (Diagram)iter.next();
                                if( nextDiagram.getType() != null &&
                                    nextDiagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID) )
                                parentPackageDiagram = nextDiagram;
                            }
                        } catch (ModelWorkspaceException e) {
                            String message = "ShowParentDiagramAction cannot find parent package diagram";  //$NON-NLS-1$
                            DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
                        }
                    } else if( parentObject == null ) {
                        // This is a package under a model, so..... get the diagram for the resource?
                        try {
                            // get diagrams and find package diagram.
                            List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(null));
                            Iterator iter = diagramList.iterator();
                            Diagram nextDiagram = null;
                            while( iter.hasNext() && parentPackageDiagram == null ) {
                                nextDiagram = (Diagram)iter.next();
                                if( nextDiagram.getType() != null &&
                                    nextDiagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID) )
                                parentPackageDiagram = nextDiagram;
                            }
                        } catch (ModelWorkspaceException e) {
                            String message = "ShowParentDiagramAction cannot find parent package diagram";  //$NON-NLS-1$
                            DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
                        }
                    }
                }
                
                
            }
        }
        
        return parentPackageDiagram;
    }

}
