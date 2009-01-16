/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
