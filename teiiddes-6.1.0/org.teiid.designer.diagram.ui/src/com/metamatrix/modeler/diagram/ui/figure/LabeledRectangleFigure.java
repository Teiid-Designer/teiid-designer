/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.figure;


import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;

/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LabeledRectangleFigure extends AbstractDiagramFigure {
    
    private Label label = null;
    private RectangleFigure rectangle = null;
    private boolean hilite = false;
    private Color defaultBkgdColor;
    
    public LabeledRectangleFigure( String labelString, Font newFont, boolean hiliteSelection, ColorPalette colorPalette ) {
        super(colorPalette);
        this.hilite = hiliteSelection;
        init(newFont);
        
        createComponent(labelString, null);        
    }
    
    
    public LabeledRectangleFigure( String labelString, boolean hiliteSelection, ColorPalette colorPalette ) {
        super(colorPalette);
        this.hilite = hiliteSelection;
        init(null);
        
        createComponent(labelString, null);        
    }
    
    
    public LabeledRectangleFigure( String labelString, Image icon, boolean hiliteSelection, ColorPalette colorPalette ) {
        super(colorPalette);
        this.hilite = hiliteSelection;
        init(null);
        
        createComponent(labelString, icon);
       
    }

    private void init(Font newFont) {
        if( newFont != null )
            this.setFont(newFont);
        else
            this.setFont(ScaledFontManager.getFont());
    }

    private void createComponent(String labelString, Image icon) {
        int hanging = 0;
        
        label = new Label(labelString);
        if( hilite )
            rectangle = new RectangleFigure();
            
        if( icon != null ) {
            label.setIcon(icon);
            label.setTextPlacement(PositionConstants.EAST);
//            label.setIconAlignment(Label.LEFT);
        }
        label.setFont(this.getFont());
        if( rectangle != null )
            this.add(rectangle);
        this.add(label);

        hanging = FigureUtilities.getFontMetrics(this.getFont()).getDescent();
        label.setForegroundColor(ColorConstants.black);
        label.setTextAlignment(PositionConstants.EAST);
        label.setRequestFocusEnabled(true);
        
        int labelWidth = FigureUtilities.getStringExtents(label.getText(), this.getFont()).width + 4;
        if (label.getIcon() != null)
            labelWidth += label.getIcon().getBounds().width + 6;
        int labelHeight = FigureUtilities.getStringExtents(label.getText(), this.getFont()).height + hanging;

        label.setSize(labelWidth, labelHeight);
        label.setPreferredSize(labelWidth, labelHeight);
        if( rectangle != null )
            rectangle.setSize(label.getSize());
        this.setSize(labelWidth, labelHeight);
        this.setPreferredSize(labelWidth, labelHeight);
        label.setLocation(new Point(1, 1));
        if( rectangle != null )
            rectangle.setLocation(new Point(0, 0));
    }
    
    public void setIcon(Image icon ) {
        if( label != null ) {
            label.setIcon(icon);
        }
    }

    public void resize() {
        Font theFont = label.getFont();
        int hanging = FigureUtilities.getFontMetrics(theFont).getDescent();
        int labelWidth = FigureUtilities.getStringExtents(label.getText(), theFont).width;
        if (label.getIcon() != null)
            labelWidth += label.getIcon().getBounds().width + 6;
        int labelHeight = FigureUtilities.getStringExtents(label.getText(), theFont).height + hanging;

        label.setSize(labelWidth, labelHeight);
        label.setPreferredSize(labelWidth, labelHeight);
        if( rectangle != null )
            rectangle.setSize(label.getSize());
        
        this.setSize(labelWidth, labelHeight);
        this.setPreferredSize(labelWidth, labelHeight);
            
        if ( DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_MODEL_NODE) &&  
             DiagramUiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.TRACE)) {
            String debugMessage = ".resize():  New Size = [" + this.getSize() + "]"; //$NON-NLS-2$ //$NON-NLS-1$
            DiagramUiConstants.Util.print(this.getClass(), debugMessage);
        }
    }
    
    public void setTextColor(Color newColor) {
        label.setForegroundColor(newColor);
    }
    
    public Label getLabel() {
        return label;
    }
    
    @Override
    public void updateForName(String newName ) {
        label.setText(newName);
        resize();
    }
    
    
    @Override
    public void updateForFont( Font newFont ) {
        if( newFont != null ) {
            this.setFont(newFont);
        } else {
            this.setFont(ScaledFontManager.getFont());
        }

        label.setFont(this.getFont());
        resize();
    }

    @Override
    public void setBackgroundColor(Color newColor ) {
        if( hilite && rectangle != null ) {
            defaultBkgdColor = newColor;
            rectangle.setBackgroundColor(defaultBkgdColor);
            rectangle.setForegroundColor(defaultBkgdColor);
        }
    }
    
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    @Override
    public void hiliteBackground(Color hiliteColor) {
        if( hiliteColor == null ) {
            rectangle.setBackgroundColor(defaultBkgdColor);
            rectangle.setForegroundColor(defaultBkgdColor);
        } else {
            rectangle.setBackgroundColor(hiliteColor);
            rectangle.setForegroundColor(hiliteColor);
        }
    }
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#refreshFont()
     */
    @Override
    public void refreshFont() {
        updateForFont(null);
    }
}

