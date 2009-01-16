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

package com.metamatrix.ui.graphics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/**
 * This class provides the Modeler UI plugins a place to cache their required colors, insure there aren't any duplicates
 * ( Based on RGB values), and insure that the colors are Disposed when the UI plugin is shut-down.
 * @since 5.0
 */
public class GlobalUiColorManager {

    private static Map<RGB, Color> fColorTable = Collections.synchronizedMap(new HashMap<RGB, Color>(10));

    /**
     * Method disposes of the colors.
     */
    public static void dispose() {
        //int nColors = fColorTable.values().size();
        Iterator<Color> e= fColorTable.values().iterator();
        while (e.hasNext()) {
            e.next().dispose();
        }

        fColorTable.clear();
        //System.out.println(" ModelerUiColorManager.dispose() called.  " + nColors + " Colors disposed");
    }
    /**
     * A getter method that returns a color.
     * @param rgb
     * @return Color
     */
    public static Color getColor(RGB rgb) {
        Color color= fColorTable.get(rgb);
        if (color == null) {
            color= new Color(Display.getCurrent(), rgb);
            //System.out.println(" ModelerUiColorManager.getColor(" + fColorTable.keySet().size()+ ")  Adding New Color = " + rgb );
            fColorTable.put(rgb, color);
        }
        return color;
    }


    public static void removeColor(RGB rgb) {
        Color color = getColor(rgb);
        if( color != null ) {
            color.dispose();
            fColorTable.remove(rgb);
        }
    }

    public static int getNumberOfColors() {
        return fColorTable.values().size();
    }
}
