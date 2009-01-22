/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.navigation.part.NavigationContainerNodeEditPart;
import com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart;
import com.metamatrix.modeler.relationship.ui.navigation.selection.*;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationGraphicalViewer extends ScrollingGraphicalViewer {
	private NavigationEditor editor;

	public NavigationGraphicalViewer(NavigationEditor editor) {
		super();
		this.editor = editor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
    public ISelection getSelection() {
		// Let's interecept the selection in the viewer and get the "ModelObjects" 
		if (getSelectedEditParts().isEmpty() && getContents() != null) {
			return super.getSelection();
		}
		
		if( getSelectedEditParts().size() == 1 ) {
			Object oneObject = getSelectedEditParts().get(0);
			if (oneObject instanceof NavigationContainerNodeEditPart ) {
				NavigationContainerNodeEditPart navEditPart = (NavigationContainerNodeEditPart)oneObject;
				// We need to take the mouse event, ask the edit part for it's selected object.
				NavigationNode selectedNode = null;
				Point mousePoint = getSelectionHandler().getLastMousePoint();
				// if we find an Node, get the eObject and 
				selectedNode = ((NavigationNodeEditPart)navEditPart).getSelectedNavigationNode(mousePoint);
				if( selectedNode != null ) {
					return new StructuredSelection(selectedNode);
				}
			}
		}
		return new StructuredSelection(getSelectedModelObjects());
	}

	private List getSelectedModelObjects() {
		List modelObjects = new ArrayList(getSelectedEditParts().size());
		List selectedEPs = getSelectedEditParts();
		boolean selectedDiagram = false;

		if (selectedEPs.size() == 1) {
			// Check here to see if "Diagram" was selected
			Object oneObject = selectedEPs.get(0);
			if (oneObject instanceof DiagramEditPart
				&& ((DiagramEditPart)oneObject).getModelObject() instanceof Diagram) {
				selectedDiagram = true;
			}
		}

		if (!selectedDiagram && getSelectionHandler() != null) {
			modelObjects = new ArrayList(getSelectionHandler().getSelectedNodeObjects());
		}

		return modelObjects;
	}

	public void deselectAll(boolean fireSelectionChanged) {
		EditPart part;
		List list = primGetSelectedEditParts();
		setFocus(null);
		for (int i = 0; i < list.size(); i++) {
			part = (EditPart)list.get(i);
			part.setSelected(EditPart.SELECTED_NONE);
		}
		list.clear();

		if (fireSelectionChanged)
			fireSelectionChanged();
	}

	public NavigationSelectionHandler getSelectionHandler() {
		return editor.getSelectionHandler();
	}

	public NavigationEditor getEditor() {
		return this.editor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartViewer#select(org.eclipse.gef.EditPart)
	 */
	@Override
    public void select(EditPart editpart) {	
		super.select(editpart);

		reveal(editpart);
	}

	public void clearAllSelections(boolean fireSelection) {
		deselectAll(fireSelection);
	}

	public Rectangle2D getBounds() {
		if( getEditor() != null && getEditor().getControl() != null ) {
			int w = getEditor().getControl().getBounds().width;
			int h = getEditor().getControl().getBounds().height;
			return new Rectangle(w, h);
		}
		return new Rectangle(100, 100);
	}
}
