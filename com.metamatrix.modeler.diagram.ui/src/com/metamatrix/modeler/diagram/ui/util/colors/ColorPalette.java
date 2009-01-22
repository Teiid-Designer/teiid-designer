/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.colors;

import org.eclipse.swt.graphics.Color;

/**
 * ColorPalette
 */
public interface ColorPalette {
    public static final int PRIMARY_BKGD_COLOR_ID   = 0;
    public static final int SECONDARY_BKGD_COLOR_ID = 1;
    public static final int FOREGROUND_COLOR_ID     = 2;
    public static final int SELECTION_COLOR_ID      = 3;
    public static final int HILITE_COLOR_ID         = 4;
    public static final int OUTLINE_COLOR_ID        = 5;

    /**
     * Method 
     * @param colorId
     * @return
     */
    Color getColor(int colorId);
    
    void setColor(int colorId, Color newColor);
    
    
    void setColors(
        Color priBkgdColor, 
        Color secBkgdColor,
        Color fgdColor,
        Color selColor,
        Color hilColor,
        Color outColor);
}
