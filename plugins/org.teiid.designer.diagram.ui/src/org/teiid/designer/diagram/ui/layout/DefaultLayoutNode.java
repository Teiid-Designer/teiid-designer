/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.layout;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;

/**
 * DefaultLayoutNode
 *
 * @since 8.0
 */
public class DefaultLayoutNode implements LayoutNode {
    
    private DiagramModelNode modelNode;
    private double thisX;
    private double thisY;
    private double centerX;
    private double centerY;
    private Rectangle bounds;

    public DefaultLayoutNode(DiagramModelNode modelNode) {
        if( modelNode != null ) {
            this.modelNode = modelNode;
            centerX = modelNode.getCenterX();
            centerY = modelNode.getCenterY();
            bounds = new Rectangle(0, 0, modelNode.getWidth(), modelNode.getHeight());
        }
    }
    
    @Override
	public DiagramModelNode getModelNode() {
        return modelNode;
    }
        
    @Override
	public Rectangle getBounds() {
        return bounds;
    }
    
    @Override
	public void setCenterXY(double x, double y) {
        centerX = x;
        centerY = y;
        thisX = centerX - getWidth()/2;
        thisY = centerY - getHeight()/2;
		bounds.setLocation((int)thisX, (int)thisY);
//        System.out.println(" -->> DefaultLayoutNode.setCenterXY():  New XY Point = " + getPosition());
    }
    
    @Override
	public void setCenterX(double x) {
        centerX = x;
        thisX = centerX - getWidth()/2;
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    @Override
	public void setCenterY(double y) {
        centerY = y;
        thisY = centerY - getHeight()/2;
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    @Override
	public Point getPosition() {
        return new Point(thisX, thisY);
    }
    
    @Override
	public void setPosition(Point point ) {
        thisX = point.x;
        thisY = point.y;
        setCenterX(thisX + getWidth()/2);
        setCenterY(thisY + getHeight()/2);
		bounds.setLocation(point);
//        System.out.println(" -->> DefaultLayoutNode.setPosition():  New XY Point = " + point);
    }
    
    @Override
	public void setPosition(double x, double y) {
        thisX = x;
        thisY = y;
        setCenterX(thisX + getWidth()/2);
        setCenterY(thisY + getHeight()/2);
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    @Override
	public void setX(double x) {
        thisX = x;
        setCenterX(thisX + getWidth()/2);
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    @Override
	public void setY(double y) {
        thisY = y;
        setCenterY(thisY + getHeight()/2);
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    @Override
	public double getX() {
        return thisX;
    }
    
    @Override
	public double getY() {
        return thisY;
    }
    
    @Override
	public double getWidth() {
        return bounds.width;
    }
    
    @Override
	public double getHeight() {
        return bounds.height;
    }

    @Override
	public double getCenterX() {
        return centerX;
    }

    @Override
	public double getCenterY() {
        return centerY;
    }

    @Override
	public void setFinalPosition() {
        getModelNode().setCenterXY((int)getCenterX(), (int)getCenterY());    
    }    /**
     * @param rectangle
     */
    @Override
	public void setBounds(Rectangle rectangle) {
        bounds = rectangle;
    }

}
