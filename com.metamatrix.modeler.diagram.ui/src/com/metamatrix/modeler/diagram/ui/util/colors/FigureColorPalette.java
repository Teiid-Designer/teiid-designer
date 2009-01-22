/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.colors;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

/**
 * FigureColorPalettte
 */
public class FigureColorPalette implements ColorPalette {
    public Color primaryBkgdColor       = ColorConstants.white;
    public Color secondaryBkgdColor     = ColorConstants.white;
    public Color foregroundColor        = ColorConstants.white;
    public Color selectionColor         = ColorConstants.white;
    public Color hiliteColor            = ColorConstants.white;
    public Color outlineColor           = ColorConstants.white;
    
    /**
     * Construct an instance of FigureColorPalettte.
     * 
     */
    
    public FigureColorPalette() {
        super();

    }
    public FigureColorPalette(
        Color priBkgdColor, 
        Color secBkgdColor,
        Color fgdColor,
        Color selColor,
        Color hilColor,
        Color outColor) {
        super();
        primaryBkgdColor = priBkgdColor;
        secondaryBkgdColor = secBkgdColor;
        foregroundColor = fgdColor;
        selectionColor = selColor;
        hiliteColor = hilColor;
        outlineColor = outColor;
    }
    
    
    public void setColors(
        Color priBkgdColor, 
        Color secBkgdColor,
        Color fgdColor,
        Color selColor,
        Color hilColor,
        Color outColor) {
            
            primaryBkgdColor = priBkgdColor;
            secondaryBkgdColor = secBkgdColor;
            foregroundColor = fgdColor;
            selectionColor = selColor;
            hiliteColor = hilColor;
            outlineColor = outColor;
    }
    
    
    public void setColor(int colorId, Color newColor ) {
        switch(colorId) {
            case PRIMARY_BKGD_COLOR_ID:     { primaryBkgdColor      = newColor; } break;
            case SECONDARY_BKGD_COLOR_ID:   { secondaryBkgdColor    = newColor; } break;
            case FOREGROUND_COLOR_ID:       { foregroundColor       = newColor; } break;
            case SELECTION_COLOR_ID:        { selectionColor        = newColor; } break;
            case HILITE_COLOR_ID:           { hiliteColor           = newColor; } break;
            case OUTLINE_COLOR_ID:          { outlineColor          = newColor; } break;
            
            default:
            break;
        }
    }
    
    
    public Color getColor(int colorId ) {
        Color returnColor = ColorConstants.white;
        
        switch(colorId) {
            case PRIMARY_BKGD_COLOR_ID:     { returnColor    = primaryBkgdColor;    } break;
            case SECONDARY_BKGD_COLOR_ID:   { returnColor    = secondaryBkgdColor;  } break;
            case FOREGROUND_COLOR_ID:       { returnColor    = foregroundColor;     } break;
            case SELECTION_COLOR_ID:        { returnColor    = selectionColor;      } break;
            case HILITE_COLOR_ID:           { returnColor    = hiliteColor;         } break;
            case OUTLINE_COLOR_ID:          { returnColor    = outlineColor;        } break;
            
            default:
            break;
        }
        return returnColor;
    }
}
