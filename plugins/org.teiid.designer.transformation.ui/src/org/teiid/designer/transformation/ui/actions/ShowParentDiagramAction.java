/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.actions.DiagramEditorAction;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * ShowParentDiagramAction
 */
public class ShowParentDiagramAction extends DiagramEditorAction {
    private static final String textString = "org.teiid.designer.transformation.ui.actions.ShowParentDiagramAction.text";  //$NON-NLS-1$
    private static final String toolTipString = "org.teiid.designer.transformation.ui.actions.ShowParentDiagramAction.toolTip";  //$NON-NLS-1$
    /**
     * Construct an instance of ShowParentDiagramAction.
     * 
     */
    public ShowParentDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.UP_PACKAGE_DIAGRAM));
        setToolTipText(UiConstants.Util.getString(toolTipString));
        setText(UiConstants.Util.getString(textString));
        setEnabled(false);
    }

    /* (non-Javadoc)
     * see org.teiid.designer.ui.common.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        if( editor != null ) {
            Diagram diagram = getParentDiagram();
            if( diagram != null ) {
                // Mark current navigation location using current open object
                UiUtil.getWorkbenchPage().getNavigationHistory().markLocation(editor);
                ModelEditorManager.closeObjectEditor();
                editor.openContext(diagram);
            }
        }
        determineEnablement();
    }
    
    public void determineEnablement() {
        if( editor != null ) {
            Diagram diagram = getParentDiagram();
            if( diagram != null )
                setEnabled(true);
            else
                setEnabled(false);
        }

    }
    
    private Diagram getParentDiagram() {
        Diagram parentPackageDiagram = null;
        
        DiagramModelNode diagramNode = editor.getCurrentModel();
        
        if( diagramNode != null ) {
            Diagram currentDiagram = (Diagram)diagramNode.getModelObject();
            if( currentDiagram != null &&
                currentDiagram.eResource() != null &&
                currentDiagram.getType() != null ) {
                if( currentDiagram.getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID) ||
                    currentDiagram.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID) ) {
                    Object diagramTarget = currentDiagram.getTarget(); // SHould be virtual group
                    ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(currentDiagram);
                    if( diagramTarget != null && 
                        diagramTarget instanceof EObject && 
                        modelResource != null ) {
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
                                            nextDiagram.getType().equals(org.teiid.designer.diagram.ui.PluginConstants.PACKAGE_DIAGRAM_TYPE_ID) )
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
                                            nextDiagram.getType().equals(org.teiid.designer.diagram.ui.PluginConstants.PACKAGE_DIAGRAM_TYPE_ID) )
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
        }
        
        return parentPackageDiagram;
    }
    
}
