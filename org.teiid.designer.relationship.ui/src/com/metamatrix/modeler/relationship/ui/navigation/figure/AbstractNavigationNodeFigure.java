/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.diagram.ui.util.ToolTipUtil;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;


/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
abstract public class AbstractNavigationNodeFigure extends Ellipse implements NavigationNodeFigure {
	private ImageFigure objectIcon;
	private boolean paintBackground = true;
	private NavigationModelNode modelNode = null;
	/**
	 * 
	 */
	
	public AbstractNavigationNodeFigure(NavigationModelNode modelNode, Image image) {
		super();
		this.modelNode = modelNode;
		init(image);
	}
	
	public AbstractNavigationNodeFigure(NavigationModelNode modelNode) {
		super();
		paintBackground = false;
		this.modelNode = modelNode;
		init(null);
	}
	
	private void init(Image icon) {
		int initialSize = getNavigationModelNode().getWidth();
		if( icon != null )
			objectIcon = new ImageFigure(icon);
//		initialSize = objectIcon.getSize().width*2;
		setSize(initialSize, initialSize);
		setLineWidth(1);
		setForegroundColor(ColorConstants.darkBlue);
		if( paintBackground )
			setBackgroundColor(ColorConstants.orange);
		setFillXOR(false); // Couldn't tell if this did anything
		setOutlineXOR(false); // Couldn't tell if this did anything
		
		if( objectIcon != null) {
			this.add(objectIcon);
			int newX = 15 - objectIcon.getPreferredSize().width/2;
			int newY = 15 - objectIcon.getPreferredSize().height/2;
			objectIcon.setSize(objectIcon.getPreferredSize());
			objectIcon.setLocation(new Point(newX, newY));

		}

	}
	
	@Override
    protected boolean useLocalCoordinates(){
		return true;
	}
	
	public void layoutFigure() {
		super.layout();
	}
    
	public void activate() {
		// Default implementation does nothing;
	}
    
	public void deactivate() {
		// Default implementation does nothing;
	}
    
    
	public void updateForSize(Dimension newSize ) {
		setSize(newSize);
//		circle.repaint();
		centerIcon();
//		this.setSize(newSize);
	}
    
    
	public void updateForLocation(Point newLocation ) {
		this.setLocation(newLocation);
	}
	
	public void centerIcon() {
		if( objectIcon != null) {
			int newX = getSize().width/2 - objectIcon.getPreferredSize().width/2 + 1;
			int newY = getSize().height/2 - objectIcon.getPreferredSize().height/2 + 1;

			objectIcon.setLocation(new Point(newX, newY));
			objectIcon.repaint();
		}
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#hiliteBackground(org.eclipse.swt.graphics.Color)
	 */
	public void hiliteBackground(Color hiliteColor) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#showSelected(boolean)
	 */
	public void showSelected(boolean selected) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#updateForError(boolean)
	 */
	public void updateForError(boolean hasErrors) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#updateForFont(org.eclipse.swt.graphics.Font)
	 */
	public void updateForFont(Font font) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#updateForName(java.lang.String)
	 */
	public void updateForName(String newName) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#updateForWarning(boolean)
	 */
	public void updateForWarning(boolean hasWarnings) {
		// XXX Auto-generated method stub

	}
	

	
	public void printBounds(String prefix) {
		System.out.println(prefix + " FIGURE Bounds = " + getBounds()); //$NON-NLS-1$
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#getNavigationModelNode()
	 */
	public NavigationModelNode getNavigationModelNode() {
		return modelNode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.IFigure#setBounds(org.eclipse.draw2d.geometry.Rectangle)
	 */
	@Override
    public void setBounds(Rectangle rect) {
		setSize(this.getSize());
		centerIcon();
		// XXX Auto-generated method stub
		super.setBounds(rect);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#setSizeToMinimum()
	 */
	public void setSizeToMinimum() {
		// Let's resize this figure to be just enough to wrap the icon
		if( objectIcon != null ) {
			Dimension iconSize = objectIcon.getPreferredSize();
			Dimension size = new Dimension(iconSize.width + 5, iconSize.height + 5);
			setSize(size);
			centerIcon();
		}
		
	}
	
	/**
	 * @see org.eclipse.draw2d.IFigure#setToolTip(IFigure)
	 */
	public void setToolTip(String toolTipString) {
		super.setToolTip(createToolTip(toolTipString));
	}
	
	
	protected IFigure createToolTip(String toolTipString) {
		return ToolTipUtil.createToolTip(toolTipString);
	}

}
