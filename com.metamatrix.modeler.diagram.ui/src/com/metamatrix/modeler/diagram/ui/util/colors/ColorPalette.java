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
