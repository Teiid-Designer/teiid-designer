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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;


/** 
 * @since 5.0
 */
public class GlobalUiFontManager {

    private static Map<FontData, Font> fontTable = new HashMap<FontData, Font>(10);

    /**
     * Method disposes of the colors.
     */
    public static void dispose() {
        //int nFonts = fontTable.values().size();
        Iterator<Font> e= fontTable.values().iterator();
        while (e.hasNext()) {
            e.next().dispose();
        }
        
        fontTable.clear();
        //System.out.println(" GlobalUiFontManager.dispose() called.  " + nFonts + " Fonts disposed");
    }
    /**
     * A getter method that returns a color.
     * @param rgb
     * @return Color
     */
    public static Font getFont(FontData fData) {
        Font font = fontTable.get(fData);
        if( font != null && font.isDisposed() ) {
            fontTable.remove(fData);
            font = null;
        }
        
        if (font == null) {
            font= new Font(Display.getCurrent(), fData);
            //System.out.println(" GlobalUiFontManager.getColor(" + fontTable.keySet().size()+ ")  Adding New Font = " + fData );
            fontTable.put(fData, font);
        } 
        return font;
    }
}
