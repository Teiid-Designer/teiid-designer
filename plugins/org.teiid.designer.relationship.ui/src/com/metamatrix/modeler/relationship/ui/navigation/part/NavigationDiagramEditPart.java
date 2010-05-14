/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.LassoDragTracker;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationGraphicalViewer;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.navigation.model.DummyFocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.LabelModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationDiagramNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationDiagramEditPart extends NonFocusNodeEditPart {
	private static final int COLUMN_LAYOUT = 0;
	private static final int CIRCULAR_LAYOUT = 2;
	private static Point dummyFocusNodePoint = new Point(0, 0);

	/** Singleton instance of MarqueeDragTracker. */
	static DragTracker m_dragTracker = null;

	private String sCurrentRouterStyle = DiagramUiConstants.DiagramRouterStyles.FAN_ROUTER;

	///////////////////////////////////////////////////////////////////////////////////////////////	
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////

	public NavigationDiagramEditPart(NavigationDiagramFigureFactory figureFactory) {
		super(figureFactory);
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {

		Figure newFigure = getFigureFactory().createFigure(getModel());

		return newFigure;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 * You need to tell how children nodes will be layed out...
	**/
	@Override
    protected void createEditPolicies() {
		//		setSelectablePart(false);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NavigationDiagramXYLayoutPolicy());
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
		if (getViewer().getContents().getChildren().size() > 0)
			return true;
		return false;
	}

	/* (non-JavaDoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	**/
	@Override
    public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();

		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CHILDREN)) {
			layout();
		}
	}

	@Override
    public void layout(boolean layoutChildren) {
		if (!hasChildren())
			return;

		if (layoutChildren) {
			EditPart canvasEditPart = getViewer().getContents();
			List canvasChildren = canvasEditPart.getChildren();
			Iterator iter = canvasChildren.iterator();
			EditPart nextEditPart = null;

			while (iter.hasNext()) {
				nextEditPart = (EditPart)iter.next();
				if (nextEditPart instanceof NavigationNodeEditPart) {
					((NavigationNodeEditPart)nextEditPart).layout(layoutChildren);
				}
			}
		}
		//		  super.layout(layoutChildren);

		// Check it's model node, if was layed out, don't re-layout!!

		layout();

		// now process the Association Labels, if any:
		List arylSourceConnections = new ArrayList();
		if (!arylSourceConnections.isEmpty()) {
			Iterator itSourceConns = arylSourceConnections.iterator();

			while (itSourceConns.hasNext()) {
				NodeConnectionModel ncmSourceConn = (NodeConnectionModel)itSourceConns.next();
				ncmSourceConn.setRouterStyle(sCurrentRouterStyle);
				ncmSourceConn.layout();
			}

		}
	}

	@Override
    public void layout() {
		if (!hasChildren())
			return;

		runCircularLayout();
	}

	protected void runCircularLayout() {
		int innerPad = 10;
		int outerPad = 10;

		NavigationNodeEditPart focusPart = getFocusEditPart();
		int outerNodeDefaultDiam = 10;
		List navigationParts = getNonFocusEditParts();

		if (focusPart != null) {
			// Locate the Focus Part in the center
			int viewerDiam = NavigationLayoutUtil.getViewDiameter((NavigationGraphicalViewer)getViewer());
			focusPart.getNavigationNodeFigure().layoutFigure();
			
			((NavigationModelNode)focusPart.getModel()).setCenterXY(viewerDiam / 2,viewerDiam / 2);
			
			focusPart.updateModelSize();
			focusPart.updateModelPosition();

			outerNodeDefaultDiam = viewerDiam/2 - focusPart.getFigure().getSize().width /2 - innerPad - outerPad;

			setLabelPosition(focusPart);

			if (!navigationParts.isEmpty()) {
				Iterator iter = navigationParts.iterator();
				NavigationNodeEditPart nextPart = null;
				int iPart = 0;
				int nNodes = navigationParts.size();
				int finalNodeDiam = 10;
				Point newNodePt = null;

				while (iter.hasNext()) {
					nextPart = (NavigationNodeEditPart)iter.next();

						
					if (nextPart instanceof NavigationContainerNodeEditPart) {
						nextPart.layout();
						// Now set it's final position based on it's real diameter
						finalNodeDiam =
							((IFigure)nextPart.getNavigationNodeFigure()).getSize().width;

						Point nextCenterPt =
							getNextNodeCenterPoint(
								CIRCULAR_LAYOUT,
								nNodes,
								iPart,
								viewerDiam,
								outerNodeDefaultDiam,
								finalNodeDiam,
								outerPad,
								innerPad,
								focusPart.getFigure().getSize().width /2);
							
						newNodePt = new Point(nextCenterPt.x - finalNodeDiam / 2, nextCenterPt.y - finalNodeDiam / 2);
//						((IFigure)nextPart.getNavigationNodeFigure()).setLocation(newNodePt);
						((NavigationModelNode)nextPart.getModel()).setPosition(newNodePt);
						//						nextPart.updateModelSize();
						//						nextPart.updateModelPosition();
					}
					setLabelPosition(nextPart);
					iPart++;
				}
			}
		}
	}

	/*
	 * This method provides a method to place any navigation node (not focus) around the perimeter.
	 */
	private Point getNextNodeCenterPoint(
			int layoutType, 
			int nNodes, 
			int iNode, 
			int diameter, 
			int outerNodeDiam, 
			int actualNodeDiam, 
			int outerPad,
			int innerPad,
			int focusNodeRadius) {

		int newX = 0;
		int newY = 0;
		// calculate the radius required to keep node and focus nodes from overlapping
		int requiredCenterRad = focusNodeRadius + innerPad + actualNodeDiam/2;
		int defaultCenterRad = diameter / 2 - outerPad - outerNodeDiam / 2;
		int nodeCenterRadius = Math.max(requiredCenterRad, defaultCenterRad);

		switch (layoutType) {
			case COLUMN_LAYOUT :
				{

				}
				break;

			case CIRCULAR_LAYOUT :
				{
					double arcValueRadians = Math.PI / 2 + 2 * iNode * Math.PI / nNodes;
					newX = (int) (diameter / 2 + nodeCenterRadius * Math.cos(arcValueRadians));
					newY = (int) (diameter / 2 - nodeCenterRadius * Math.sin(arcValueRadians));
				}
				break;
		}
		return new Point(newX, newY);
	}

	private NavigationNodeEditPart getFocusEditPart() {
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			Object nextO = iter.next();
			if (nextO instanceof FocusNodeEditPart)
				return (NavigationNodeEditPart)nextO;
		}
		return null;
	}
	
	public NavigationNode getCurrentFocusNode() {
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			Object nextO = iter.next();
			if (nextO instanceof FocusNodeEditPart) {
				NavigationModelNode nmn = (NavigationModelNode)((NavigationNodeEditPart)nextO).getModel();
				return (NavigationNode)nmn.getModelObject();
			}

		}
		return null;
	}

	private List getNonFocusEditParts() {
		List returnList = null;
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			Object nextO = iter.next();
			if ((nextO instanceof NonFocusNodeEditPart
				|| nextO instanceof NavigationContainerNodeEditPart)
				&& !(nextO instanceof FocusNodeEditPart)) {
				if (returnList == null)
					returnList = new ArrayList();
				returnList.add(nextO);
			}
		}

		if (returnList == null)
			return Collections.EMPTY_LIST;

		return returnList;
	}
	
	private NavigationNodeEditPart getNonFocusEditPart(NavigationNode someNavNode) {
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			Object nextO = iter.next();
			if ((nextO instanceof NavigationContainerNodeEditPart)) {
				// Get the list of children
				NavigationNode nextNavNode;
				Object nextObj = null;
				Iterator iter2 = ((EditPart)nextO).getChildren().iterator();
				while( iter2.hasNext() ) {
					nextObj = iter2.next();
					if( nextObj instanceof NonFocusNodeEditPart ) {
						nextNavNode = (NavigationNode)((NavigationModelNode)((NavigationNodeEditPart)nextObj).getModel()).getModelObject();
						if( nextNavNode.getModelObjectUri().equals(someNavNode.getModelObjectUri()) )
							return (NavigationNodeEditPart)nextObj;
					}
					
				}
			}
		}
		
		return null;
	}
	
	private NavigationNodeEditPart getDummyFocusEditPart() {
		Iterator iter = getChildren().iterator();
		while (iter.hasNext()) {
			Object nextO = iter.next();
			if ((nextO instanceof DummyFocusNodeEditPart)) {
				return (NavigationNodeEditPart)nextO;

			}
		}
		
		return null;
	}

	private LabelEditPart getLabelForNode(NavigationNodeEditPart nodePart) {
		LabelModelNode labelNode =
			(LabelModelNode) ((NavigationModelNode)nodePart.getModel()).getLabelNode();
		if (labelNode != null) {
			Iterator iter = getChildren().iterator();
			Object nextPart = null;
			LabelEditPart labelEP = null;
			while (iter.hasNext()) {
				nextPart = iter.next();
				if (nextPart instanceof LabelEditPart) {
					labelEP = (LabelEditPart)nextPart;
					if (labelEP.getModel() != null && labelEP.getModel().equals(labelNode))
						return labelEP;
				}
			}
		}

		return null;
	}

	private void setLabelPosition(NavigationNodeEditPart navPart) {
		LabelEditPart labelPart = getLabelForNode(navPart);
		if (labelPart != null) {
			NavigationModelNode navModelNode = ((NavigationModelNode)navPart.getModel());
			LabelModelNode labelModelNode = (LabelModelNode)labelPart.getModel();
			int startX = navModelNode.getCenterX();
			int startY = navModelNode.getCenterY();
			// let's get a point that's just off the lower right radius (approx. 315 degrees);
			double angle45 = Math.PI/4;
			double radius = navModelNode.getWidth()/2;
			
			startX = startX + (int)(radius*Math.sin(angle45));
			startY = startY + (int)(radius*Math.sin(angle45));
			labelModelNode.setPosition(new Point(startX, startY));
			labelPart.resetSize();
		}
	}

	public boolean hasConnections() {
		EditPart canvasEditPart = getViewer().getContents();
		Iterator iter = canvasEditPart.getChildren().iterator();
		DiagramEditPart nextEditPart = null;

		while (iter.hasNext()) {
			nextEditPart = (DiagramEditPart)iter.next();
			if (!nextEditPart.getSourceConnections().isEmpty()
				|| !nextEditPart.getTargetConnections().isEmpty())
				return true;
		}
		return false;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	**/
	@Override
    protected void refreshVisuals() {
		//		if( getFigure() != null ) {
		//			int w = (int)((NavigationGraphicalViewer)getViewer()).getBounds().getWidth();
		//			int h = (int)((NavigationGraphicalViewer)getViewer()).getBounds().getHeight();
		//			getFigure().setSize( new Dimension(w, h));
		//		}

		ConnectionLayer cLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		if (sCurrentRouterStyle.equals(DiagramUiConstants.DiagramRouterStyles.FAN_ROUTER)) {
			cLayer.setConnectionRouter(new FanRouter());
		}
	}

	public void updateForPreferences() {
		RGB currentBkgdColor =
			PreferenceConverter.getColor(
				DiagramUiPlugin.getDefault().getPreferenceStore(),
				PluginConstants.Prefs.Appearance.RELATIONSHIP_BKGD_COLOR);
		this.getFigure().setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
		layout(false);
	}
	
	public void animateForwardNavigation(NavigationNode targetNavigationNode) {
		// let's create a dummy focus node here
		NavigationNodeEditPart focusPart = getFocusEditPart();
			
		NonFocusNodeEditPart targetNonFocusNodeEP = (NonFocusNodeEditPart)getNonFocusEditPart(targetNavigationNode);
		
		if( focusPart != null && targetNonFocusNodeEP != null ) {

			NavigationContainerNodeEditPart containerEP = (NavigationContainerNodeEditPart)targetNonFocusNodeEP.getParent();
			Point endPoint1 = containerEP.getFigure().getBounds().getLocation();
			Point nodePoint = targetNonFocusNodeEP.getFigure().getBounds().getLocation();
			endPoint1.x = nodePoint.x + endPoint1.x + targetNonFocusNodeEP.getFigure().getBounds().width/2;
			endPoint1.y = nodePoint.y + endPoint1.y + targetNonFocusNodeEP.getFigure().getBounds().height/2;
			
			Point startingPoint1 = focusPart.getFigure().getBounds().getLocation();
			startingPoint1.x += focusPart.getFigure().getBounds().width/2;
			startingPoint1.y += focusPart.getFigure().getBounds().height/2;
			Dimension endingSize = focusPart.getFigure().getBounds().getSize();
			
			// Create, resize, locate and Add.
			DummyFocusModelNode dfmn1 = new DummyFocusModelNode( (NavigationDiagramNode)getModel() );
			dfmn1.setPosition(startingPoint1);
			dfmn1.setSize(new Dimension(2, 2));
			((NavigationDiagramNode)getModel()).addChild(dfmn1);

			((AbstractNavigationEditPart)getDummyFocusEditPart()).animateMove(startingPoint1, endPoint1, 6, endingSize.width, true);
			dfmn1.setPosition(startingPoint1);
			dfmn1.setSize(endingSize);

			endPoint1.x = startingPoint1.x - 40  - endingSize.width/2;
			endPoint1.y = startingPoint1.y - endingSize.height/2;
			dummyFocusNodePoint.x = endPoint1.x;
			dummyFocusNodePoint.y = endPoint1.y;
			((AbstractNavigationEditPart)getDummyFocusEditPart()).animateMove(endPoint1, startingPoint1);
			((NavigationDiagramNode)getModel()).removeChild(dfmn1);
		}
	}
	public void animateBackNavigation(NavigationNode targetNavigationNode) {
		// let's create a dummy focus node here
		NavigationNodeEditPart nonFocusPart = getNonFocusEditPart(targetNavigationNode);
		FocusNodeEditPart targetFocusNodeEP = (FocusNodeEditPart)getFocusEditPart();
		
		if( nonFocusPart != null && targetFocusNodeEP != null ) {
			Color savedColor = nonFocusPart.getFigure().getBackgroundColor();
			nonFocusPart.getFigure().setBackgroundColor(UiConstants.Colors.NON_FOCUS_NODE_BKGD);
			
			Point endPoint = targetFocusNodeEP.getFigure().getBounds().getLocation();
			endPoint.x += targetFocusNodeEP.getFigure().getBounds().width/2;
			endPoint.y += targetFocusNodeEP.getFigure().getBounds().height/2;

			NavigationContainerNodeEditPart containerEP = (NavigationContainerNodeEditPart)nonFocusPart.getParent();
			Point startingPoint = containerEP.getFigure().getBounds().getLocation();
			Point nodePoint = nonFocusPart.getFigure().getBounds().getLocation();
			startingPoint.x = nodePoint.x + startingPoint.x + nonFocusPart.getFigure().getBounds().width/2;
			startingPoint.y = nodePoint.y + startingPoint.y + nonFocusPart.getFigure().getBounds().height/2;
			
			// Create, resize, locate and Add.
			DummyFocusModelNode dfmn = new DummyFocusModelNode( (NavigationDiagramNode)getModel() );
			dfmn.setPosition(dummyFocusNodePoint);
			Dimension startingSize = nonFocusPart.getFigure().getBounds().getSize();
			dfmn.setSize(startingSize);
			((NavigationDiagramNode)getModel()).addChild(dfmn);
			

			((AbstractNavigationEditPart)getDummyFocusEditPart()).animateMove(startingPoint, dummyFocusNodePoint, startingSize.width, 6, false);
			nonFocusPart.getFigure().setBackgroundColor(savedColor);
			((NavigationDiagramNode)getModel()).removeChild(dfmn);
		}
	}
}
