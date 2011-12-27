/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.relationship.ui.actions.RelationshipAction;
import com.metamatrix.modeler.relationship.ui.custom.CustomDiagramModelFactory;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
/**
 * RemoveTransformationSource
 */
public class RemoveFromDiagramAction extends RelationshipAction {
    private DiagramEditor editor;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public RemoveFromDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.REMOVE_FROM_DIAGRAM));
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
                modelFactory.remove(SelectionUtilities.getSelectedEObjects(getSelection()), diagramNode);
            }
        }
    }
    
    
    private boolean shouldEnable() {
        if( ! (this.getPart() instanceof ModelEditor) || 
            getSelection() == null ||
            SelectionUtilities.getSelectedEObjects(getSelection()).size() < 0)
            return false;
            
        return allSelectedInDiagram() && isWritable();
    }
    
    private boolean allSelectedInDiagram() {
        // check the diagram to see if all selected objects are in diagram??
        if(    editor != null 
            && editor.getDiagramViewer() != null
            && editor.getDiagramViewer().getSelectionHandler() != null ) {
            List selectedEObjects = editor.getDiagramViewer().getSelectionHandler().getSelectedEObjects();
			if( selectedEObjects == null || selectedEObjects.isEmpty() )
				return false;
   
            Iterator iter = SelectionUtilities.getSelectedEObjects(getSelection()).iterator();
            EObject eObj = null;
            while( iter.hasNext() ) {
                eObj = (EObject)iter.next();
                if( ! selectedEObjects.contains(eObj))
                    return false;
            }
        }
        
        return true;
    }
    
    public void setDiagramEditor(DiagramEditor editor) {
        this.editor = editor;
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
