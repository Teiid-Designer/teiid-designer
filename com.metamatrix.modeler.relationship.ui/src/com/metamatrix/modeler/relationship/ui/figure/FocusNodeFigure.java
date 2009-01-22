/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigure;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FocusNodeFigure extends AbstractDiagramFigure {
//	private ImageFigure errorIcon;
//	private ImageFigure warningIcon;
	private ImageFigure objectIcon;

	private Ellipse circle;

	/**
	 * 
	 */
	public FocusNodeFigure(Image icon, ColorPalette colorPalette) {
		super(colorPalette);

		init(icon, colorPalette);

		createComponent();
	}

	private void init(Image icon, ColorPalette colorPalette) {
		if( icon != null )
			objectIcon = new ImageFigure(icon);
			
		circle = new Ellipse();
		circle.setLineWidth(1);
		circle.setForegroundColor(ColorConstants.darkBlue);
		circle.setBackgroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
		this.add(circle);
		if( objectIcon != null ) {
			this.add(objectIcon);
			int newX = 15 - objectIcon.getPreferredSize().width/2;
			int newY = 15 - objectIcon.getPreferredSize().height/2;
			objectIcon.setSize(objectIcon.getPreferredSize());
			objectIcon.setLocation(new Point(newX, newY));
		}
	}

	private void createComponent() {
		setInitialSize();
		resetIconLocations();
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure#setSizeToMinimum()
	 */
	public void setSizeToMinimum() {
		// Let's resize this figure to be just enough to wrap the icon
		if( objectIcon != null ) {
			Dimension iconSize = objectIcon.getPreferredSize();
			Dimension size = new Dimension(iconSize.width + 5, iconSize.height + 5);
			circle.setSize(size);
			centerIcon();
			this.setSize(size);
		}
		
	}

	private void setInitialSize() {
		setSizeToMinimum();
	}

	@Override
    protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
    public void layoutFigure() {
		resetIconLocations();
	}

	private void resetIconLocations() {
//		if (errorIcon != null) {
//			errorIcon.setLocation(new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
//		}
//		if (warningIcon != null) {
//			warningIcon.setLocation(
//				new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
//		}
		centerIcon();
	}

	@Override
    public void updateForSize(Dimension newSize) {
		circle.setSize(newSize);
		resetIconLocations();
		//			  layoutFigure();
	}

	
	public void centerIcon() {
		if( objectIcon != null) {
			int newX = circle.getSize().width/2 - objectIcon.getPreferredSize().width/2 + 1;
			int newY = circle.getSize().height/2 - objectIcon.getPreferredSize().height/2 + 1;

			objectIcon.setLocation(new Point(newX, newY));
			objectIcon.repaint();
		}
	}


	@Override
    public void updateForError(boolean hasErrors) {
		if (hasErrors) {
//			if (errorIcon == null) {
//				errorIcon =
//					new ImageFigure(
//						DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON));
//				if (errorIcon != null) {
//					this.add(errorIcon);
//					errorIcon.setLocation(
//						new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
//					errorIcon.setSize(errorIcon.getPreferredSize());
//				}
//			}
//		} else if (errorIcon != null) {
//			this.remove(errorIcon);
//			errorIcon = null;
		}
	}

	@Override
    public void updateForWarning(boolean hasWarnings) {
		if (hasWarnings) {
//			if (warningIcon == null) {
//				warningIcon =
//					new ImageFigure(
//						DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON));
//				if (warningIcon != null) {
//					this.add(warningIcon);
//					warningIcon.setLocation(
//						new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
//					warningIcon.setSize(warningIcon.getPreferredSize());
//				}
//			}
//		} else if (warningIcon != null) {
//			this.remove(warningIcon);
//			warningIcon = null;
		}
	}
}
