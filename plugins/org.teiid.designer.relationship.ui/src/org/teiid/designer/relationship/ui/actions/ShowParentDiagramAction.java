/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.actions.DiagramEditorAction;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.relationship.Relationship;
import org.teiid.designer.metamodels.relationship.RelationshipFolder;
import org.teiid.designer.relationship.ui.PluginConstants;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.relationship.ui.diagram.RelationshipDiagramUtil;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * ShowParentDiagramAction
 */
public class ShowParentDiagramAction extends DiagramEditorAction {
	private static final String textString = "org.teiid.designer.relationship.ui.actions.ShowParentDiagramAction.text";  //$NON-NLS-1$
	private static final String toolTipString = "org.teiid.designer.relationship.ui.actions.ShowParentDiagramAction.toolTip";  //$NON-NLS-1$

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
            Diagram diagram = getParentPackageDiagram();
            if( diagram != null ) {
                // Mark current navigation location using current open object
                UiUtil.getWorkbenchPage().getNavigationHistory().markLocation(editor);
                ModelEditorManager.closeObjectEditor();
                editor.openContext(diagram);
            }
        }
        determineEnablement();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
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
        Diagram parentDiagram = null;
        DiagramModelNode diagramNode = editor.getCurrentModel();
        if( diagramNode != null ) {
            Diagram currentDiagram = (Diagram)diagramNode.getModelObject();
            if( currentDiagram != null &&
                currentDiagram.getType() != null && 
                currentDiagram.getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID) ) {
                Object diagramTarget = currentDiagram.getTarget();
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(currentDiagram);
                if( diagramTarget != null && 
                    diagramTarget instanceof EObject && 
                    modelResource != null &&
                    !(diagramTarget instanceof ModelAnnotation)) {
                    EObject targetEObject = (EObject)diagramTarget;
					EObject parentEObject = null;
					
                    if( targetEObject instanceof RelationshipFolder) {
                    	if( targetEObject.eContainer() != null) {
							parentEObject = targetEObject.eContainer();
							}
                    } else if( targetEObject instanceof Relationship ) {
						parentEObject = targetEObject.eContainer();
                    }
                    
					parentDiagram = RelationshipDiagramUtil.getRelationshipDiagram(modelResource, parentEObject, this, true);
                }
                
                
            }
        }
        
        return parentDiagram;
    }

}
