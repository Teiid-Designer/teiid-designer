/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigure;
import com.metamatrix.modeler.diagram.ui.figure.LabeledRectangleFigure;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.relationship.ui.PluginConstants;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipTypeFigure extends AbstractDiagramFigure implements DirectEditFigure {
	private ImageFigure errorIcon;
	private ImageFigure warningIcon;

//	private Polygon outlineFigure;
	private LabeledRectangleFigure typeLabel;
	private LabeledRectangleFigure nameLabel;
	private LabeledRectangleFigure roleLabel;
	private Image nameIcon;
	private RoundedRectangle baseRectangle;
	private int cornerRadius = 18;
	private static final int ySpacing = 2;
	private static final int xInset = 6;
	private int nameFontStyle = ScaledFont.PLAIN_STYLE;
	
	/**
	 * 
	 */
	public RelationshipTypeFigure(
		String type,
		String name,
		String role,
		Image icon,
		ColorPalette colorPalette) {
		super(colorPalette);
		nameIcon = icon;
		init(type, name, role, colorPalette);
	}

	private void init(
		String type,
		String name,
		String role,
		ColorPalette colorPalette) {
		
		baseRectangle = new RoundedRectangle();
		baseRectangle.setCornerDimensions(new Dimension(cornerRadius, cornerRadius));
		
		baseRectangle.setLineWidth(1);
		baseRectangle.setForegroundColor(ColorConstants.darkBlue);
		baseRectangle.setBackgroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
		this.add(baseRectangle);
		
		createTypeLabel(type);
		
		createNameLabel(name);
		
		createRoleLabel(role);
		
		refreshFont();
		
		setInitialSize();
	}
	
	private void createNameLabel(String newName) {
		if (newName != null) {
			if (nameLabel == null) {
				if (nameIcon != null)
					nameLabel = new LabeledRectangleFigure(newName, nameIcon, true, getColorPalette());
				else
					nameLabel = new LabeledRectangleFigure(newName, true, null);
				this.add(nameLabel);
			} else {
				nameLabel.getLabel().setText(newName);
			}
			nameLabel.layoutFigure();
			nameLabel.setBackgroundColor(baseRectangle.getBackgroundColor());
		}
	}
	
	private void createRoleLabel(String role) {
		if (role != null) {
			if (roleLabel == null) {
				roleLabel = new LabeledRectangleFigure(role, true, getColorPalette());
				this.add(roleLabel);
			} else {
				roleLabel.getLabel().setText(role);
			}
			roleLabel.setTextColor(ColorConstants.darkGray);
			roleLabel.setBackgroundColor(baseRectangle.getBackgroundColor());
		}
	}
	
	private void createTypeLabel(String type) {
		if (type != null) {
			if (typeLabel == null) {
				typeLabel = new LabeledRectangleFigure(type, true, getColorPalette());
				this.add(typeLabel);
			} else {
				typeLabel.getLabel().setText(type);
			}
			typeLabel.setTextColor(ColorConstants.darkGray);
			typeLabel.setBackgroundColor(baseRectangle.getBackgroundColor());
		}
	}

	private void setInitialSize() {
		int maxWidth = 2 * cornerRadius;
		int maxHeight = 2 * cornerRadius;
		int deltaHeight = ySpacing;

		if (typeLabel != null) {
			deltaHeight += typeLabel.getBounds().height + ySpacing;
			maxWidth = Math.max(maxWidth, typeLabel.getSize().width);
		}
		if (nameLabel != null) {
			deltaHeight += nameLabel.getBounds().height + ySpacing;
			maxWidth = Math.max(maxWidth, nameLabel.getSize().width);
		}

		if (roleLabel != null) {
			deltaHeight += roleLabel.getBounds().height;
			maxWidth = Math.max(maxWidth, roleLabel.getSize().width);
		}

		maxWidth += xInset * 4;
		maxHeight = Math.max(maxHeight, deltaHeight);
		maxHeight += 8;

		baseRectangle.setSize(maxWidth, maxHeight);
		this.setSize(maxWidth, maxHeight);
	}

	@Override
    protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
    public void layoutFigure() {
		// Need to resize package to fit header
		int centerX = getSize().width / 2;

		int currentY = ySpacing * 2;
		if (typeLabel != null) {
			typeLabel.setLocation(
				new Point(centerX - typeLabel.getBounds().width / 2, currentY));
			currentY += typeLabel.getBounds().height + ySpacing;
		}

		if (nameLabel != null) {
			nameLabel.setLocation(new Point(centerX - nameLabel.getBounds().width / 2, currentY));
			currentY += nameLabel.getBounds().height + ySpacing;
		}

		if (roleLabel != null) {
			roleLabel.setLocation(
				new Point(centerX - roleLabel.getBounds().width / 2, currentY));
		}
		resetIconLocations();
	}

	private void resetIconLocations() {
		if (errorIcon != null) {
			errorIcon.setLocation(new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
		}
		if (warningIcon != null) {
			warningIcon.setLocation(
				new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
		}
	}

	@Override
    public void updateForSize(Dimension newSize) {
		baseRectangle.setSize(newSize);
		resetIconLocations();
		//			  layoutFigure();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#updateForName(java.lang.String)
	 */
	@Override
    public void updateForName(String newName) {
		if( nameLabel == null && newName != null ) {
			createNameLabel(newName);
		} else {
			nameLabel.updateForName(newName);
		}
		setInitialSize();
		layoutFigure();
	}
	
	public void updateForChange(String newRoleString) {
		if( roleLabel != null )
		roleLabel.updateForName(newRoleString);
		setInitialSize();
		layoutFigure();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#refreshFont()
	 */
	@Override
    public void refreshFont() {
		Font nameFont = ScaledFontManager.getFont(nameFontStyle);
		Font smallerFont = getSmallerFont();

		if (typeLabel != null)
			typeLabel.updateForFont(smallerFont);
		if (nameLabel != null)
			nameLabel.updateForFont(nameFont);
		if (roleLabel != null)
			roleLabel.updateForFont(smallerFont);
		layoutFigure();
		setInitialSize();
		updateForSize(this.getSize());
	}

	private Font getSmallerFont() {
		return ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
//		int iCurrGeneralFontSize = ScaledFontManager.getSize();
//		int iNewLabelFontSize = 0;
//
//		if (ScaledFontManager.canDecrease(iCurrGeneralFontSize - 1)) {
//			// we can decrease by 2
//			iNewLabelFontSize = iCurrGeneralFontSize - 2;
//		} else if (ScaledFontManager.canDecrease(iCurrGeneralFontSize)) {
//			// we can only decrease by 1
//			iNewLabelFontSize = iCurrGeneralFontSize - 1;
//		} else {
//			// no room to decrease at all
//			iNewLabelFontSize = iCurrGeneralFontSize;
//		}
//
//		// construct the new font
//		Font smallerFont =
//			new Font(
//				null,
//				ScaledFontManager.getName(),
//				iNewLabelFontSize,
//				ScaledFontManager.getStyle());
//
//		return smallerFont;
	}

	@Override
    public void updateForError(boolean hasErrors) {
		if (hasErrors) {
			if (errorIcon == null) {
				errorIcon =
					new ImageFigure(
						DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON));
				if (errorIcon != null) {
					this.add(errorIcon);
					errorIcon.setLocation(
						new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
					errorIcon.setSize(errorIcon.getPreferredSize());
				}
			}
		} else if (errorIcon != null) {
			this.remove(errorIcon);
			errorIcon = null;
		}
	}

	@Override
    public void updateForWarning(boolean hasWarnings) {
		if (hasWarnings) {
			if (warningIcon == null) {
				warningIcon =
					new ImageFigure(
						DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON));
				if (warningIcon != null) {
					this.add(warningIcon);
					warningIcon.setLocation(
						new Point(nameLabel.getLocation().x, nameLabel.getLocation().y));
					warningIcon.setSize(warningIcon.getPreferredSize());
				}
			}
		} else if (warningIcon != null) {
			this.remove(warningIcon);
			warningIcon = null;
		}
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure#getLabelFigure()
	 */
	public Label getLabelFigure() {
		return nameLabel.getLabel();
	}

	public void setNameFontStyle(int style) {
		nameFontStyle = style;
		refreshFont();
	}
}
