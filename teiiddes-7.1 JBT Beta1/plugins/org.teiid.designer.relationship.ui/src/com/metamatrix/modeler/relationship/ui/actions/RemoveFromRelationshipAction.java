/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.relationship.RelationshipEditor;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.connection.RelationshipLink;
import com.metamatrix.modeler.relationship.ui.model.RelationshipModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemoveFromRelationshipAction extends RelationshipAction {
	private DiagramEditor editor;
	private RelationshipEditor reEditor;
	private static final String ACTION_DESCRIPTION = "Remove From Relationship"; //$NON-NLS-1$
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////

	public RemoveFromRelationshipAction() {
		super();
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.REMOVE_FROM_RELATIONSHIP));
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
			// Now we need to wrap this???
			
			boolean canUndo = true;
			
			List selectedEObjects = editor.getDiagramViewer().getSelectionHandler().getSelectedEObjects();
			if( selectedEObjects != null && !selectedEObjects.isEmpty() ) {
				boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
				boolean succeeded = false;
				try {
					removeParticipantsFromRelationships(selectedEObjects);
					succeeded = true;
				} finally {
					if (requiredStart) {
						if(succeeded) {
							ModelerCore.commitTxn();
							if( !canUndo)
								ModelerUndoManager.getInstance().clearAllEdits();
						} else {
							ModelerCore.rollbackTxn();
						}
					}
					setEnabled(false);
				}
			}
		}
	}
    
    
	private boolean shouldEnable() {
		return diagramNotEmpty() && isWritable() && selectedObjectsOk();
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
	
	private boolean selectedObjectsOk() {
		boolean allOK = true;
		List selectedEObjects = editor.getDiagramViewer().getSelectionHandler().getSelectedEObjects();
		if( selectedEObjects != null && !selectedEObjects.isEmpty() ) {
			Iterator iter = selectedEObjects.iterator();
			Object nextObject = null;
			while( iter.hasNext() && allOK)  {
				nextObject = iter.next();
				if( nextObject instanceof Relationship || nextObject instanceof RelationshipType)
					allOK = false;
			}
		} else allOK = false;
		return allOK;
	}
	
	private void removeParticipantsFromRelationships(List selectedEObjects) {
		DiagramModelNode currentDiagram = editor.getCurrentModel();
		if( selectedEObjects != null && !selectedEObjects.isEmpty() ) {
			try {
				Iterator iter = selectedEObjects.iterator();
				Object nextObject = null;
				while( iter.hasNext() )  {
					nextObject = iter.next();
					if( !(nextObject instanceof Relationship) &&  !(nextObject instanceof RelationshipType) ) {
						removeObjectFromRelationships((EObject)nextObject, currentDiagram);
					}
				}
			} catch (ModelerCoreException e) {
				// XXX Auto-generated catch block
				e.printStackTrace();
			} finally {
				reEditor = null;
			}
		}

	}
	
	private void removeObjectFromRelationships(EObject nextObject, DiagramModelNode currentDiagram) throws ModelerCoreException {
		// First get the DiagramModelObject
		DiagramModelNode focusNode = DiagramUiUtilities.getDiagramModelNode(nextObject, currentDiagram);
		if( focusNode != null ) {

			// Get other end of source connections (should be RelationshipModelNode)
			List sourceConnections = focusNode.getSourceConnections();
			RelationshipLink nextLink = null;
			Iterator iter = sourceConnections.iterator();
			DiagramModelNode targetNode = null;
			while( iter.hasNext() ) {
				nextLink = (RelationshipLink)iter.next();
				targetNode = (DiagramModelNode)nextLink.getTargetNode();
				if( targetNode instanceof RelationshipModelNode ) {
					getRelationshipEditor((Relationship)targetNode.getModelObject()).removeSourceParticipant(nextObject);
				}
			}
			
			// Get other end of source connections (should be RelationshipModelNode)
			List targetConnections = focusNode.getTargetConnections();
			iter = targetConnections.iterator();
			DiagramModelNode sourceNode = null;
			while( iter.hasNext() ) {
				nextLink = (RelationshipLink)iter.next();
				sourceNode = (DiagramModelNode)nextLink.getSourceNode();
				if( sourceNode instanceof RelationshipModelNode ) {
					getRelationshipEditor((Relationship)sourceNode.getModelObject()).removeTargetParticipant(nextObject);
				}
			}

		}
	}
	
	private RelationshipEditor getRelationshipEditor(Relationship rel) {
		if( reEditor == null )
			reEditor = RelationshipPlugin.createEditor( rel );
		else if( ! reEditor.getRelationship().equals(rel) )
			reEditor = RelationshipPlugin.createEditor( rel );
			
		return reEditor;
	}
}
