/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.relationship.ui.part;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.layout.LayoutHelper;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractFreeEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramXYLayoutEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.LassoDragTracker;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.layout.RelationshipLayoutHelper;
import com.metamatrix.modeler.ui.IDiagramTypeEditPart;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipDiagramEditPart extends AbstractDiagramEditPart
                                       implements IDiagramTypeEditPart {

	/** Singleton instance of MarqueeDragTracker. */
	static DragTracker m_dragTracker = null;
    
	private String sCurrentRouterStyle = DiagramUiConstants.DiagramRouterStyles.FAN_ROUTER;

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {

		Figure newFigure = getFigureFactory().createFigure(getModel());
		setCurrentDiagramFont(ScaledFontManager.getFont());
		return newFigure;
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 * You need to tell how children nodes will be layed out...
	**/
	@Override
    protected void createEditPolicies() {
		setSelectablePart(false);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy());
	}

	/**
	 * This method is not mandatory to implement, but if you do not implement
	 * it, you will not have the ability to rectangle-selects several figures...
	**/
	@Override
    public DragTracker getDragTracker(Request req) {
		// Unlike in Logical Diagram Editor example, I use a singleton because this 
		// method is Entered  >>  several time, so I prefer to save memory ; and it works!
		if (m_dragTracker == null) {
			m_dragTracker = new LassoDragTracker();
		}
		return m_dragTracker;
	}
    
	private boolean hasChildren() {
		if( getViewer().getContents().getChildren().size() > 0 )
			return true;
		return false;
	}
    
	/* (non-JavaDoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	**/
	@Override
    public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
//		String prop = evt.getPropertyName();

//		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CHILDREN)) {
//			layout();
//		}
	}

	@Override
    public void layout(boolean layoutChildren) {
		if( !hasChildren() )
			return;
            
		if( layoutChildren) {
			EditPart canvasEditPart = getViewer().getContents();
			List canvasChildren = canvasEditPart.getChildren();
			Iterator iter = canvasChildren.iterator();
			EditPart nextEditPart = null;
                
			while (iter.hasNext()) {
				nextEditPart = (EditPart)iter.next();
				if ( nextEditPart instanceof DiagramEditPart ) {
					((DiagramEditPart)nextEditPart).layout(layoutChildren);
				}
			}
		}
//		  super.layout(layoutChildren);
            
		// Check it's model node, if was layed out, don't re-layout!!
        
		if( !((DiagramModelNode)getModel()).wasLayedOut() ) {
			layout();
		} else {
			((DiagramModelNode)getModel()).recoverObjectProperties();
		}
          
		// now process the Association Labels, if any:
//		List arylSourceConnections = new ArrayList();
//		if ( !arylSourceConnections.isEmpty() ) {
//			Iterator itSourceConns = arylSourceConnections.iterator();
//            
//			while( itSourceConns.hasNext() ) {
//				NodeConnectionModel ncmSourceConn = (NodeConnectionModel)itSourceConns.next();
//				ncmSourceConn.setRouterStyle( sCurrentRouterStyle );                
//				ncmSourceConn.layout();                                    
//			}
//        
//		}
	}
    
	@Override
    public void layout() {
		if( !hasChildren() )
			return;
		// Let's check the current selection.  If only one edit part is selected and it is a relationship
		// then we get it's model object and pass it it.
		DiagramModelNode selectedModelNode = null;
		if( getViewer().getSelectedEditParts().size() == 1 ) {
			Object selectedPart = getViewer().getSelectedEditParts().get(0);
			if( selectedPart instanceof RelationshipNodeEditPart ) {
				selectedModelNode = (DiagramModelNode)((DiagramEditPart)selectedPart).getModel();
			}
			else if(  selectedPart instanceof RelationshipTypeNodeEditPart ) {
				selectedModelNode = (DiagramModelNode)((DiagramEditPart)selectedPart).getModel();
			}
		}

		
		LayoutHelper layoutHelper = new RelationshipLayoutHelper((DiagramModelNode)getModel(), selectedModelNode);
		layoutHelper.layoutAll();

   
		// Update Anchors and Links
		updateAnchorsAndLinks();
	}
    
	private void updateAnchorsAndLinks() {
		EditPart canvasEditPart = getViewer().getContents();
		List canvasChildren = canvasEditPart.getChildren();
		Iterator iter = canvasChildren.iterator();
		EditPart nextEditPart = null;
            
		while (iter.hasNext()) {
			nextEditPart = (EditPart)iter.next();
			if ( nextEditPart instanceof DiagramEditPart && ! (nextEditPart instanceof AbstractFreeEditPart )) {
				((DiagramEditPart)nextEditPart).createOrUpdateAnchorsLocations(false);
			}
		}
	}
    
	public boolean hasConnections() {
		EditPart canvasEditPart = getViewer().getContents();
		Iterator iter = canvasEditPart.getChildren().iterator();
		DiagramEditPart nextEditPart = null;

		while (iter.hasNext()) {
			nextEditPart = (DiagramEditPart)iter.next();
			if( !nextEditPart.getSourceConnections().isEmpty() ||
				!nextEditPart.getTargetConnections().isEmpty() )
				return true;
		}
		return false;
	}
    
	/** 
	 * Reset font from Font Managar and call layout on all diagram objects.
	**/
	@Override
    public void refreshFont(boolean refreshChildren) {
		// Diagram needs to also do a layout here
		super.refreshFont(refreshChildren);
        
		// But not at the diagram level
        
		if( hasChildren()) {
			EditPart canvasEditPart = getViewer().getContents();
			List canvasChildren = canvasEditPart.getChildren();
			Iterator iter = canvasChildren.iterator();
			EditPart nextEditPart = null;
            
			while (iter.hasNext()) {
				nextEditPart = (EditPart)iter.next();
				if ( nextEditPart instanceof DiagramEditPart ) {
					((DiagramEditPart)nextEditPart).layout(true);
				}
			}
		}
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	**/
	@Override
    protected void refreshVisuals() {
		ConnectionLayer cLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		if ( sCurrentRouterStyle.equals( DiagramUiConstants.DiagramRouterStyles.FAN_ROUTER ) ) {        
			cLayer.setConnectionRouter(new FanRouter());
		} 
	}
    
    
    
	@Override
    public void updateForPreferences() {
		RGB currentBkgdColor = 
			PreferenceConverter.getColor(
				DiagramUiPlugin.getDefault().getPreferenceStore(),
				PluginConstants.Prefs.Appearance.RELATIONSHIP_BKGD_COLOR);
		this.getFigure().setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
		refreshFont(true);
		layout(false);
	}
}
