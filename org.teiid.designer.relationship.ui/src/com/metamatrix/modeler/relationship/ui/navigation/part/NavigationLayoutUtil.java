/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationGraphicalViewer;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationLayoutUtil {

	/*
	 * This method provides a method to place any navigation node (not focus) around the perimeter.
	 */
	public static Rectangle getNextCircularNodePoint( 
			final int nNodes, 
			final int iNode, 
			final int parentDiameter,
			final int innerPad,
			final int outerPad) {
		
		int newX = 0;
		int newY = 0;
		int tempNNodes = nNodes;
		if( tempNNodes < 4 ) {
			tempNNodes = 4;
		}
		
		double arcValueRadians = Math.PI/2 + 2*iNode*Math.PI/nNodes;
		double deltaArcInRadians = 2*Math.PI/tempNNodes;
		
		int nodeCenterRadius = (int)((parentDiameter/2 - outerPad)/(1 + Math.sin(deltaArcInRadians/2)));
//		int outerNodeRadius = parentDiameter/2 - outerPad - nodeCenterRadius;
		int outerNodeRadius = (int)(nodeCenterRadius*Math.sin(deltaArcInRadians/2));
		if( innerPad > 0 && innerPad < outerNodeRadius) {
			outerNodeRadius = outerNodeRadius - innerPad;
			nodeCenterRadius = nodeCenterRadius + innerPad/2;
		}

		newX = (int)(parentDiameter/2 + nodeCenterRadius*Math.cos(arcValueRadians)) - outerNodeRadius;
		newY = (int)(parentDiameter/2 - nodeCenterRadius*Math.sin(arcValueRadians)) - outerNodeRadius;

		return new Rectangle(new Point(newX, newY), new Dimension(outerNodeRadius*2, outerNodeRadius*2));
	}
	
	/*
	 * This method provides a method to place any navigation node (not focus) around the perimeter.
	 */
	public static Rectangle getNextCircularNodePoint( 
			final int nNodes, 
			final int iNode, 
			final int parentDiameter,
			final int outerPad) {
		
		int newX = 0;
		int newY = 0;
		
		double arcValueRadians = Math.PI/2 + 2*iNode*Math.PI/nNodes;
		double deltaArcInRadians = 2*Math.PI/nNodes;
		
		int nodeCenterRadius = (int)((parentDiameter/2 - outerPad)/(1 + Math.sin(deltaArcInRadians/2)));
		int outerNodeRadius = parentDiameter/2 - outerPad - nodeCenterRadius;

		newX = (int)(parentDiameter/2 + nodeCenterRadius*Math.cos(arcValueRadians)) - outerNodeRadius;
		newY = (int)(parentDiameter/2 - nodeCenterRadius*Math.sin(arcValueRadians)) - outerNodeRadius;

		return new Rectangle(new Point(newX, newY), new Dimension(outerNodeRadius*2, outerNodeRadius*2));
	}
	
	public static int getViewDiameter(NavigationGraphicalViewer viewer) {
			int viewerDiameter = 10;
			int viewerWidth = (int)viewer.getBounds().getWidth();
			int viewerHeight = (int)viewer.getBounds().getHeight();

			viewerDiameter = Math.min(viewerWidth, viewerHeight);
			return viewerDiameter;
	}

}
