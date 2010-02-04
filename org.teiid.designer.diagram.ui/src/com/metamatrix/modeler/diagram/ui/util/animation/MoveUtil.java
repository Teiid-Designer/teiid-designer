/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.animation;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MoveUtil {
	public static final int deltaX = 2;
	public static final int deltaY = 2;
	

	/**
	 * 
	 */
	public static void animateMove(final DiagramEditPart dep) {
		animate(dep);
	}
	
	private static void animate(final DiagramEditPart dep) {
		
		Dimension size = ((DiagramModelNode) dep.getModel()).getSize();
		
		// we need to establish a path fromt he current location and the new location

		Point oldLoc = new Point(dep.getFigure().getBounds().x, dep.getFigure().getBounds().y);
		Point newLoc = ((DiagramModelNode) dep.getModel()).getPosition();
		Point tempLoc = new Point(oldLoc);
		
		boolean isRight = newLoc.x >= oldLoc.x;
		boolean isDown = newLoc.y >= oldLoc.y;
		int dx = deltaX;
		int dy = deltaY;
		if( !isRight )
			dx = dx*(-1);
		if( !isDown )
			dy = dy*(-1);
			
		boolean xArrived = false;
		boolean yArrived = false;
		while( !xArrived || !yArrived ) {
			if( Math.abs(tempLoc.x - newLoc.x) > deltaX ) {
				tempLoc.x += dx;
			} else {
				xArrived = true;
			}

			if( Math.abs(tempLoc.y - newLoc.y) > deltaY )  {
				tempLoc.y += dy;
			} else  {
				yArrived = true;
			}
			if( !xArrived || !yArrived ) {
				Rectangle r = new Rectangle(tempLoc, size);
				((GraphicalEditPart) dep.getParent()).setLayoutConstraint(dep, dep.getFigure(), r);
//				dep.getFigure().repaint();
				dep.getContentPane().getUpdateManager().performUpdate(r);
//				scrolledCanvas.setRedraw(true);
//				scrolledCanvas.getContents().repaint();
//				System.out.println(" --- MoveUtil.animate()");
			}
//			try {
//				Thread.sleep(5);
//			} catch (InterruptedException e1) {
//			}
		}
	}
	
	/**
	 * 
	 */
	public static void animateMove(DiagramModelNode dmn, Point oldLoc, Point newLoc) {
		
		// we need to establish a path fromt he current location and the new location

		Point tempLoc = new Point(oldLoc);
		
		boolean isRight = newLoc.x >= oldLoc.x;
		boolean isDown = newLoc.y >= oldLoc.y;
		int dx = deltaX;
		int dy = deltaY;
		if( !isRight )
			dx = dx*(-1);
		if( !isDown )
			dy = dy*(-1);
			
		boolean xArrived = false;
		boolean yArrived = false;
		while( !xArrived && !yArrived ) {
			if( Math.abs(tempLoc.x - newLoc.x) > Math.abs(deltaX) ) {
				tempLoc.x += dx;
			} else {
				xArrived = true;
			}

			if( Math.abs(tempLoc.y - newLoc.y) > Math.abs(deltaY) )  {
				tempLoc.y += dy;
			} else  {
				yArrived = true;
			}
			if( !xArrived || !yArrived ) {
				dmn.setPosition(tempLoc);
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
//				String message = this.getClass().getName() + ":  initializeDiagram() error sleeping on Thread"; //$NON-NLS-1$
//				DiagramUiPlugin.Util.log(IStatus.ERROR, e1, message);
			}
		}
	}

}
