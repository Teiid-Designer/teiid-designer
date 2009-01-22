/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * Colors used in the Reconciler
 */
public class ColorManager {

    public static final RGB BOUND_BACKGROUND = new RGB(255, 255, 255);
    public static final RGB UNBOUND_BACKGROUND = new RGB(245,215,230);
    public static final RGB TYPE_CONFLICT_BACKGROUND = new RGB(245,215,230);

    //protected Map fColorTable = new HashMap(10);

    /**
     * Method disposes of the colors.
     */
    public void dispose() {
        // NO OP since we are now delegating to the ModelerUiColorManager
    }
    /**
     * A getter method that returns a color.
     * @param rgb
     * @return Color
     */
    public Color getColor(RGB rgb) {
        return GlobalUiColorManager.getColor(rgb);
    }
}
