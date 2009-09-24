/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.MouseEvent;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationGraphicalViewer;
import com.metamatrix.modeler.relationship.ui.navigation.model.FocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart;
import com.metamatrix.modeler.relationship.ui.part.FocusNodeEditPart;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationSelectionHandler implements INavigationDoubleClickListener {
	private NavigationGraphicalViewer viewer;
	private List dClickListeners;
	private Point lastMousePoint = new Point(0, 0);
	
	/**
	 * Construct an instance of DiagramSelectionHandler.
	 * 
	 */
	public NavigationSelectionHandler(GraphicalViewer viewer) {
		super();
		this.viewer = (NavigationGraphicalViewer)viewer;
	}
    
	public NavigationGraphicalViewer getViewer() {
		return this.viewer;
	}
    
	public void deselectAll() {
		if( ! getViewer().getSelectedEditParts().isEmpty() ) {
			getViewer().deselectAll();
		}
	}
    
	public void select(EObject selectedObject ) {

		EditPart selectedPart = findEditPart(selectedObject);
		if( selectedPart != null ) {
			deselectAll();
			getViewer().select(selectedPart);
		}  
	}
	
	public void select(NavigationNode selectedObject ) {

		DiagramEditPart selectedPart = findEditPart(selectedObject);
		if( selectedPart != null ) {
			deselectAll();
			getViewer().select(selectedPart);
		}  
	}
    
	private boolean allSelectedAreEObjects(List objects) {
		Iterator iter = objects.iterator();
		Object obj = null;
		while( iter.hasNext() ) {
			obj = iter.next();
			if( !(obj instanceof EObject) ) {
				return false;
			}
		}
		return true;
	}
    
    
	public List getSelectedNodeObjects() {
		List selectedNodeObjects = new ArrayList();
		Iterator iter = getViewer().getSelectedEditParts().iterator();
		Object obj = null;
		while( iter.hasNext() ) {
			obj = iter.next();
			if( obj instanceof NavigationNodeEditPart ) {
				Object nodeObj = ((NavigationNodeEditPart)obj).getModel();
				if( nodeObj != null ) {
					Object navNode = ((NavigationModelNode)nodeObj).getModelObject();
					if( navNode != null && !selectedNodeObjects.contains(navNode) )
						selectedNodeObjects.add(navNode); 
				}
					
			}
		}
		return selectedNodeObjects;
	}
    
	public void select(ISelection selection ) {
		List selectedObjects = SelectionUtilities.getSelectedObjects(selection);
		// Now let's make sure they are all eObjects.
		if( allSelectedAreEObjects(selectedObjects) ) {
			Iterator iter = selectedObjects.iterator();
			Object obj = null;
			while( iter.hasNext() ) {
				obj = iter.next();
				if( obj instanceof EObject ) {
					select((EObject)obj);
				}
			}
		}
 
	}
    
	public void clearDependencyHilites() {
		// get all parts and call clearHiliting();
		if( viewer != null && viewer.getRootEditPart() != null ) {
    
			List contents = getViewer().getRootEditPart().getChildren();
    
			Iterator iter = contents.iterator();
            
			Object nextObj = null;
            
			while( iter.hasNext() ) {
				nextObj = iter.next();
				if( nextObj instanceof DiagramEditPart ) {
					((DiagramEditPart)nextObj).clearHiliting();
				}
			}
		}
	}
    

	public void selectAndReveal(EObject selectedObject ) {
    
	}
    
	public DiagramEditPart findEditPart(NavigationNode selectedNavNode) {
		DiagramEditPart matchingPart = null;
        
		if( viewer != null && viewer.getRootEditPart() != null ) {
    
			Iterator iter = getViewer().getRootEditPart().getChildren().iterator();
    		EditPart rootPart = null;
    		
    		if( iter.hasNext() ) {
				rootPart = (EditPart)iter.next();
    		}
    		if( rootPart != null ) {
				iter = rootPart.getChildren().iterator();
	            
				EditPart nextPart = null;
	            
				while( iter.hasNext() && matchingPart == null ) {
					nextPart = (EditPart)iter.next();
	
					if( nextPart instanceof FocusNodeEditPart  &&
						((FocusModelNode)nextPart.getModel()).getModelObject().equals(selectedNavNode)) {
							matchingPart = (DiagramEditPart)nextPart;
					}
				}
			}
		}
        
		return matchingPart;
	}
	
	public EditPart findEditPart(EObject selectedObject) {
		EditPart matchingPart = null;
        
		if( viewer != null && viewer.getRootEditPart() != null ) {
    
			List contents = getViewer().getRootEditPart().getChildren();
    
			Iterator iter = contents.iterator();
            
			Object nextObj = null;
    
            
			while( iter.hasNext() && matchingPart == null ) {
				nextObj = iter.next();

				if( nextObj instanceof DiagramEditPart ) {
					if( selectedObject instanceof ModelAnnotation )
						matchingPart = (DiagramEditPart)nextObj;
					else
						matchingPart = ((DiagramEditPart)nextObj).getEditPart(selectedObject, false);
				}
			}
		}
        
		return matchingPart;
	}
	
	public void handleDoubleClick( MouseEvent me, EditPart navEditPart ) {
		if( navEditPart != null ) {
			// We need to take the mouse event, ask the edit part for it's selected object.
			NavigationNode selectedNode = null;
			
			// if we find an Node, get the eObject and 
			selectedNode = ((NavigationNodeEditPart)navEditPart).getSelectedNavigationNode(me);
			if( selectedNode != null ) {
				deselectAll();
				handleDoubleClick(selectedNode);
			}
		}
	}
    
	public boolean handleDoubleClick( NavigationNode selectedNavNode ) {
		boolean handledHere = false;
		
		if( selectedNavNode != null ) {
			doubleClick(new NavigationDoubleClickEvent(this, new StructuredSelection(selectedNavNode)));
			handledHere = true;
		}
		
		return handledHere;
	}
    
	public boolean shouldReveal(EObject eObject) {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(NavigationDoubleClickEvent event) {
		if( dClickListeners != null && !dClickListeners.isEmpty() ) {
			((INavigationDoubleClickListener)dClickListeners.get(0)).doubleClick(event);
		}
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.views.ModelViewer#addModelObjectDoubleClickListener(org.eclipse.jface.viewers.IDoubleClickListener)
	 */
	public void addDoubleClickListener(INavigationDoubleClickListener listener) {
		if( dClickListeners == null ) {
			dClickListeners = new ArrayList(1);
		}
		dClickListeners.add(listener);
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.views.ModelViewer#removeModelObjectDoubleClickListener(org.eclipse.jface.viewers.IDoubleClickListener)
	 */
	public void removeDoubleClickListener(INavigationDoubleClickListener listener) {
		dClickListeners.remove(listener);
	}
	/**
	 * @param point
	 */
	public void setLastMousePoint(Point point) {
		lastMousePoint = point;
	}

	/**
	 * @return
	 */
	public Point getLastMousePoint() {
		return lastMousePoint;
	}

}
