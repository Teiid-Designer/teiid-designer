/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.relationship.ui.actions.RelationshipAction;
import com.metamatrix.modeler.relationship.ui.custom.CustomDiagramModelFactory;

/**
 * ClearTransformation
 */
public class ClearDiagramAction extends RelationshipAction {
    private DiagramEditor editor;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ClearDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.CLEAR_DIAGRAM));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
	@Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        
        setEnabled(shouldEnable());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        if( editor != null ) {
    
            // Need to get the current diagram
            DiagramModelNode diagramNode = editor.getCurrentModel();
            // Need to get ahold of the CustomDiagramModelFactory
            CustomDiagramModelFactory modelFactory = (CustomDiagramModelFactory)editor.getModelFactory();
            // And call add(SelectionUtilities.getSelectedEObjects(getSelection())
            
            if( diagramNode != null && modelFactory != null ) {
                modelFactory.clear(diagramNode);
            }
        }
    }
    
    
    private boolean shouldEnable() {
        return diagramNotEmpty() && isWritable();
    }
    
    public void setDiagramEditor(DiagramEditor editor) {
        this.editor = editor;
    }
    
    private boolean diagramNotEmpty() {
        if( editor != null ) {
            DiagramModelNode currentDiagram = editor.getCurrentModel();
            if( currentDiagram != null &&
            	currentDiagram.getChildren() != null && 
                ! currentDiagram.getChildren().isEmpty() &&
                currentDiagram.getChildren().size() > 0 )
                return true;
        }
            
        return false;
    }
    
    private boolean isWritable() {
        if( editor != null ) {
            DiagramModelNode currentDiagram = editor.getCurrentModel();
            if( currentDiagram != null ) {
                EObject diagram = currentDiagram.getModelObject();
                if( !ModelObjectUtilities.isReadOnly(diagram)) {
                    return true;
                }
            }
        }
        return false;
    }
}
