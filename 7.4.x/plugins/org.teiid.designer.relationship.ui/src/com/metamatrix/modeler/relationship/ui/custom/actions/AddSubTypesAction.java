/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.actions.RelationshipAction;
import com.metamatrix.modeler.relationship.ui.custom.CustomDiagramModelFactory;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AddSubTypesAction extends RelationshipAction {
	private static final String textString = "com.metamatrix.modeler.relationship.ui.custom.actions.AddSubTypesAction.text";  //$NON-NLS-1$
	private static final String toolTipString = "com.metamatrix.modeler.relationship.ui.custom.actions.AddSubTypesAction.toolTip";  //$NON-NLS-1$

	private DiagramEditor editor;
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public AddSubTypesAction() {
		super();
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.SHOW_SUBTYPES));
		setToolTipText(UiConstants.Util.getString(toolTipString));
		setText(UiConstants.Util.getString(textString));
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	@Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
		// initialize abstract base class info
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
				RelationshipType relType = (RelationshipType)SelectionUtilities.getSelectedEObject(getSelection());
				List subTypes = removeExistingTypes(diagramNode, modelFactory, relType.getSubType());
				modelFactory.add(subTypes, diagramNode);
			}
		}
	}
    
	public void setDiagramEditor(DiagramEditor editor) {
		this.editor = editor;
	}
    
	private boolean shouldEnable() {
		boolean enable = false;
        
		enable = isWritable() && selectedObjectsAreValid();
        
		return enable;
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
    
	private boolean selectedObjectsAreValid() {
		boolean value = false;
		if( SelectionUtilities.isSingleSelection(getSelection())) {
			// only if selected a single RelationshipType object
			Object nextObject = SelectionUtilities.getSelectedEObject(getSelection());
			if( nextObject != null && nextObject instanceof RelationshipType ) {
				value = subTypesExistAndNotShown();
			}
		}
		return value;
	}
	
	private boolean subTypesExistAndNotShown() {
		if( editor != null ) {
    
			// Need to get the current diagram
			DiagramModelNode diagramNode = editor.getCurrentModel();
			// Need to get ahold of the CustomDiagramModelFactory
			CustomDiagramModelFactory modelFactory = (CustomDiagramModelFactory)editor.getModelFactory();
			// And call add(SelectionUtilities.getSelectedEObjects(getSelection())
            
			if( diagramNode != null && modelFactory != null ) {
				EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
				if( eObject instanceof RelationshipType ) {
					RelationshipType relType = (RelationshipType)eObject;
					List subTypes = removeExistingTypes(diagramNode, modelFactory, relType.getSubType());
					if( subTypes != null && !subTypes.isEmpty())
						return true;
				}
			}
		}
		return false;
	}
	
	private List removeExistingTypes(DiagramModelNode diagramNode, CustomDiagramModelFactory modelFactory, List typesList) {
		List newList = new ArrayList(typesList.size());
		
		Iterator iter = typesList.iterator();
		DiagramModelNode existingNode = null;
		EObject nextType = null;
		while( iter.hasNext() ) {
			nextType = (EObject)iter.next();
			existingNode = modelFactory.getNodeInDiagram(diagramNode, nextType);
			if( existingNode == null ) {
				newList.add(nextType);
			}
		}
		
		return newList;
	}
}
