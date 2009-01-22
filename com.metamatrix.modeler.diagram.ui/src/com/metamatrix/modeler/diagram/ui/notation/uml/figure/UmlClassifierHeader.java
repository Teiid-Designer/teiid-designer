/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SimpleRaisedBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;

/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UmlClassifierHeader extends RectangleFigure {
    private Label stereotypeLabel;
    private Label nameLabel;
    private Label locationLabel;
    private Color defaultBkgdColor;
    private int nameFontStyle = ScaledFont.PLAIN_STYLE;

    private int topInset = 4;
    private int bottomInset = 4;
    private int editIconSize = 16;
    private int leftButtonSize = 0;
    private boolean hasLeftButton = false;
    private IFigure expandcontrol;
    
    
    
    /**
     * 
     */
    public UmlClassifierHeader(String stereotype, String name, String location, Image icon, Color fgdColor) {
        super();
        this.setForegroundColor(fgdColor);
        init(stereotype, name, location, icon);
        createComponent();
    }

    public void setDefaultBkgdColor(Color bkgdColor) {
        defaultBkgdColor = bkgdColor;
        this.setBackgroundColor(defaultBkgdColor);
    }

    private void init(String stereotype, String name, String location, Image icon) {
        this.setBorder(new SimpleRaisedBorder());
        if( hasLeftButton )
            leftButtonSize = editIconSize;
        else
            leftButtonSize = 0;
        
        if (stereotype != null) {
            if (stereotypeLabel == null) {
                stereotypeLabel = new Label(stereotype);
                this.add(stereotypeLabel);
            } else {
                stereotypeLabel.setText(stereotype);
            }
            stereotypeLabel.setForegroundColor(ColorConstants.darkGray);
        }

        if (name != null) {
            if (nameLabel == null) {
                if( icon != null ) {
                    nameLabel = new Label(name);
                    nameLabel.setIcon(icon);
                } else
                    nameLabel = new Label(name);
                this.add(nameLabel);
            } else {
                nameLabel.setText(name);
            }
            nameLabel.setForegroundColor(ColorConstants.black);
        }
        if (location != null) {
            if (locationLabel == null) {
                locationLabel = new Label(location);
                this.add(locationLabel);
            } else {
                locationLabel.setText(location);
            }
            locationLabel.setForegroundColor(ColorConstants.darkGray);
        }
        
        //Now Initialize Fonts and Sizes
        refreshFont();
    }

    private void createComponent() {
        
        this.setSize(getInitialMinimumSize());

        layoutThisFigure();
    }

    public void layoutThisFigure() {
        int centerX = leftButtonSize + (this.getSize().width - leftButtonSize) / 2;
        int currentY = topInset;

        if (stereotypeLabel != null) {
            stereotypeLabel.setLocation(new Point(centerX - stereotypeLabel.getBounds().width / 2, currentY));
            currentY += stereotypeLabel.getBounds().height + leftButtonSize/4;
        }
        if (nameLabel != null) {
            nameLabel.setLocation(new Point(centerX - nameLabel.getBounds().width / 2, currentY));
            currentY += nameLabel.getBounds().height;
        }
        if (locationLabel != null) {
            locationLabel.setLocation(new Point(centerX - locationLabel.getBounds().width / 2, currentY));
            currentY += nameLabel.getBounds().height;
        }
        currentY += bottomInset;
        this.setSize(this.getSize().width, currentY);
    }
    
    public Dimension getInitialMinimumSize() {
        int maxWidth = 10 + leftButtonSize;
        if (stereotypeLabel != null)
            maxWidth = Math.max(maxWidth, stereotypeLabel.getBounds().width + leftButtonSize);
        if (nameLabel != null)
            maxWidth = Math.max(maxWidth, nameLabel.getBounds().width + leftButtonSize);
        if (locationLabel != null)
            maxWidth = Math.max(maxWidth, locationLabel.getBounds().width + leftButtonSize);
        maxWidth += 20;

        int rectHeight = topInset + leftButtonSize/4;
        if (nameLabel != null)
            rectHeight += nameLabel.getBounds().height;
        if (stereotypeLabel != null)
            rectHeight += stereotypeLabel.getBounds().height;
        if (locationLabel != null)
            rectHeight += locationLabel.getBounds().height;
        rectHeight += bottomInset;
        return new Dimension( maxWidth, rectHeight);
    }

    @Override
    protected boolean useLocalCoordinates() {
        return true;
    }

    public Label getNameLabel() {
        return nameLabel;
    }
    
    public IFigure getNameFigure() {
        return nameLabel; 
    }

    public void refreshName(String newName) {
        init(null, newName, null, null);
        createComponent();
    }
    

    public void refreshPath(String newPath) {
        init(null, null, newPath, null);
        createComponent();
    }
    
    private void setLabelSize(Label label) {
        Font theFont = label.getFont();
        int hanging = FigureUtilities.getFontMetrics(theFont).getDescent();
        int labelWidth = FigureUtilities.getStringExtents(label.getText(), theFont).width;
        if (label.getIcon() != null)
            labelWidth += label.getIcon().getBounds().width + 30;
        int labelHeight = FigureUtilities.getStringExtents(label.getText(), theFont).height + hanging;

        label.setSize(labelWidth, labelHeight);
        label.setPreferredSize(labelWidth, labelHeight);
    }

    public void refreshFont() {
        // always run this; it may have changed since the last time layout was called...
        Font smallerFont = getSmallerFont();
		Font nameFont = ScaledFontManager.getFont(nameFontStyle);
        if (stereotypeLabel != null) {
            stereotypeLabel.setFont(smallerFont);
            setLabelSize(stereotypeLabel);
        }
        if (nameLabel != null) {
        	nameLabel.setFont(nameFont);
            setLabelSize(nameLabel);
        }            
        if (locationLabel != null) {
            locationLabel.setFont(smallerFont);
            setLabelSize(locationLabel);
        }            
            
        createComponent();
    }

    private Font getSmallerFont() {
		return ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
    }
    
    public void hiliteBackground(Color hiliteColor) {
        if( hiliteColor == null )
            this.setBackgroundColor(defaultBkgdColor);
        else
            this.setBackgroundColor(hiliteColor);
        
        nameLabel.setForegroundColor(ColorConstants.black);
    }
	/**
	 * @param b
	 */
	public void setNameFontStyle(int style) {
		nameFontStyle = style;
	}

    public void setLeftButton(boolean leftButton) {
        this.hasLeftButton = leftButton;
        
        if ( expandcontrol != null ) {
            leftButtonSize = expandcontrol.getSize().width;
        }
        
        if( hasLeftButton ) {
            leftButtonSize += editIconSize;
        } 
    }

    public void setExpandControl(IFigure expandcontrol) {
        this.expandcontrol = expandcontrol;
        
        if( hasLeftButton ) {
            leftButtonSize = editIconSize + 3 + expandcontrol.getSize().width;
        } else
            leftButtonSize = expandcontrol.getSize().width;
    }
    
    @Override
    public void paint(Graphics graphics) {
        graphics.pushState();
        graphics.setForegroundColor(getLocalBackgroundColor());
        graphics.setBackgroundColor(getLocalForegroundColor());
        graphics.fillGradient(this.getBounds(), true);
        this.paintChildren(graphics);
        graphics.popState();
        graphics.restoreState();
    }
}
