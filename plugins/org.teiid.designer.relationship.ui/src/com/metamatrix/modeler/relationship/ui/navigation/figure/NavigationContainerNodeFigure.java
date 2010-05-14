/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.figure;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import com.metamatrix.modeler.diagram.ui.util.ToolTipUtil;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationContainerModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationContainerNodeFigure extends AbstractNavigationNodeFigure {
	private int numContainedItems = 0;
	private Vector containedItems;
	/**
	 * 
	 */

	public NavigationContainerNodeFigure(
		NavigationContainerModelNode modelNode,
		String toolTipString) {
		super(modelNode);
		setToolTip(toolTipString);
		init();
	}

	private void init() {
		setBackgroundColor(UiConstants.Colors.RELATIONSHIP_BKGD);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#layoutFigure()
	 */
	@Override
    public void layoutFigure() {
		// We need to move all figures here to forefront!!!!
		numContainedItems = 0;
		containedItems = new Vector();

		// This constainer should have NavigationNodeFigure type children
		// Reconcile 

		List childFigures = getChildren();
		Iterator iter = childFigures.iterator();
		Object nextObject = null;
		while (iter.hasNext()) {
			nextObject = iter.next();
			if (nextObject instanceof NavigationNodeFigure
				&& !containedItems.contains(nextObject)) {
				containedItems.add(nextObject);
				numContainedItems++;
			} else if (nextObject instanceof FocusNodeFigure) {
				((FocusNodeFigure)nextObject).layoutFigure();
			}
		}

		// Now let's walk through the objects and assume that the container size is fixed.

		iter = containedItems.iterator();
		NavigationNodeFigure nextFigure = null;
		int nFigures = containedItems.size();
		int iFigure = 0;
		Rectangle newBounds = null;
		Point newLocation = null;
		int nodeDiameter = 10;
		int thisDiameter = -1;

		while (iter.hasNext()) {
			nextFigure = (NavigationNodeFigure)iter.next();
			//			newBounds = NavigationLayoutUtil.getNextCircularNodePoint(nFigures, iFigure, this.getSize().width, 5, 4);
			//			((IFigure)nextFigure).setBounds(newBounds);

			//			nextFigure.setSizeToMinimum();
			 ((NonFocusNodeFigure)nextFigure).layoutFigure();
			nodeDiameter = ((NonFocusNodeFigure)nextFigure).getSize().width;
			if (thisDiameter < 0) {
				thisDiameter = getContainerDiameter(nFigures, nodeDiameter, 5);
				this.setSize(thisDiameter, thisDiameter);
			}
			newLocation = getNodePosition(nFigures, iFigure, nodeDiameter, 5);

			newBounds = new Rectangle(newLocation.x, newLocation.y, nodeDiameter, nodeDiameter);
			((IFigure)nextFigure).setBounds(newBounds);
			((IFigure)nextFigure).repaint();
			iFigure++;
		}

	}

	public NavigationModelNode getSelectedModelNode(Point selectedPoint) {
		Point localPoint =
			new Point(selectedPoint.x - getBounds().x, selectedPoint.y - getBounds().y);
		NavigationModelNode modelNode = null;
		// We need to move all figures here to forefront!!!!
		numContainedItems = 0;
		containedItems = new Vector();

		// This constainer should have NavigationNodeFigure type children
		// Reconcile 

		List childFigures = getChildren();
		Iterator iter = childFigures.iterator();
		Object nextObject = null;
		while (iter.hasNext()) {
			nextObject = iter.next();
			if (nextObject instanceof NavigationNodeFigure
				&& !containedItems.contains(nextObject)) {
				containedItems.add(nextObject);
				numContainedItems++;
			}
		}

		// Now let's walk through the objects and assume that the container size is fixed.

		iter = containedItems.iterator();
		NavigationNodeFigure nextFigure = null;
		while (iter.hasNext() && modelNode == null) {
			nextFigure = (NavigationNodeFigure)iter.next();
			if (((IFigure)nextFigure).getBounds().contains(localPoint)) {
				modelNode = nextFigure.getNavigationModelNode();
			}
		}

		return modelNode;
	}

	public int getContainerDiameter(int nNodes, int nodeDiameter, int padding) {
		int diameter = nodeDiameter + padding * 2;

		if (nNodes > 1 && nNodes < 5 ) {
			diameter = nodeDiameter * 2 + 4 * padding;
		} else if (nNodes == 1) {
			// do nothing
		} 
		else {
			// nNodes > 4
			double deltaArcInRadians = 2 * Math.PI / nNodes;
			int minRad = (int) ((nodeDiameter + padding)/(2*Math.sin(deltaArcInRadians/2)));
			diameter = 2*(minRad+ nodeDiameter/2 + padding);
		}

		return diameter;
	}

	public Point getNodePosition(int nNodes, int iNode, int nodeDiameter, int padding) {
		Point centerPoint = null;
		int containerDiameter = this.getSize().width;
		int paddedDiameter = nodeDiameter + 2 * padding;
		int nodeCenterRadius = containerDiameter / 2 - paddedDiameter / 2;

		if (nNodes == 1) {
			centerPoint =
				new Point(
					paddedDiameter / 2 - nodeDiameter / 2,
					paddedDiameter / 2 - nodeDiameter / 2);
		} else {
			// nNodes > 1
			double arcValueRadians = Math.PI / 2 + 2 * iNode * Math.PI / nNodes;

			int newX =
				(int) (containerDiameter / 2 + nodeCenterRadius * Math.cos(arcValueRadians))
					- nodeDiameter / 2;
			int newY =
				(int) (containerDiameter / 2 - nodeCenterRadius * Math.sin(arcValueRadians))
					- nodeDiameter / 2;
			centerPoint = new Point(newX, newY);
		}

		return centerPoint;
	}
	
	@Override
    protected IFigure createToolTip(String toolTipString) {
		return ToolTipUtil.createToolTip(toolTipString);
	}
}
