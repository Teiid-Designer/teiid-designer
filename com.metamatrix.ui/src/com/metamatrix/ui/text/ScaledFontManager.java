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

package com.metamatrix.ui.text;

import org.eclipse.swt.graphics.Font;

/**
 * ScaledFontManager
 */
public class ScaledFontManager {

    private static final int DEFAULT_FONT_SIZE = 10;
    private static final int MIN_FONT_SIZE = 4;
    private static final int MAX_FONT_SIZE = 40;

    private static int currentSize = DEFAULT_FONT_SIZE;
    private static String currentName = "Veranda";  //$NON-NLS-1$
    private static int currentStyle = 0;

    private Font currentFont;


    /**
     * Construct an instance of ScaledFontManager.
     *
     */
    public ScaledFontManager() {
        super();
    }

    public Font getCurrentFont() {
        if ( currentFont == null ) {
            currentFont = new Font(null, currentName, DEFAULT_FONT_SIZE, currentStyle);
        }
        return currentFont;
    }

    public Font createFontOfSize( int iSize ) {
    	boolean createFont = true;

    	if (currentFont != null) {
    		if (currentFont.getFontData()[0].getHeight() == iSize) {
        		createFont = false;
        	} else {
        		currentFont.dispose();
        	}
    	}

        if (createFont) {
            currentFont = new Font(null, currentName, iSize, currentStyle);
        }

        return currentFont;
    }

    public void increase() {
        int currSize = getSize();
        if (currSize < MAX_FONT_SIZE) {
            setSize(currSize + 1);
        }
    }

    public void decrease() {
        int currSize = getSize();
        if (currSize > MIN_FONT_SIZE) {
            setSize(currSize - 1);
        }
    }

    public boolean canIncrease() {
        int currSize = getSize();
        if (currSize < MAX_FONT_SIZE)
            return true;
        return false;
    }


    public boolean canIncrease( int iSize ) {

        if (iSize < MAX_FONT_SIZE)
            return true;
        return false;
    }


    public boolean canDecrease() {
        int currSize = getSize();
        if (currSize  > MIN_FONT_SIZE)
            return true;
        return false;
    }


    public boolean canDecrease( int iSize ) {

        if (iSize > MIN_FONT_SIZE)
            return true;
        return false;
    }

    public Font getFont() {
        return currentFont;
    }

    public String getName() {
        return currentName;
    }

    public int getSize() {
        return currentSize;
    }

    public int getStyle() {
        return currentStyle;
    }

    public void setFont(String typeName, int size, int style) {
        setName(typeName);
        setSize(size);
        setStyle(style);
        resetFont();
    }


    public void setFont(Font newFont) {
        currentFont = newFont;
    }

    public void setName(String newName) {
        currentName = newName;
    }

    public void setStyle(int newStyle) {
        currentStyle = newStyle;
        resetFont();
    }

    public void setSize(int newSize) {
        currentSize = newSize;
        resetFont();
    }

    public void resetFont() {
        setFont( new Font(null, getName(), getSize(), getStyle()) );
    }
}
