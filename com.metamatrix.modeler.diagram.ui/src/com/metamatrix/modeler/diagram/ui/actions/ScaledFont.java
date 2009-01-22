/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.swt.graphics.Font;

/**
 * ScaledFont
 */
public interface ScaledFont {
	static final int PLAIN_STYLE = 0;
	static final int BOLD_STYLE = 1;
	static final int ITALICS_STYLE = 2;
	static final int BOLD_ITALICS_STYLE = 3;
	static final int SMALLER_PLAIN_STYLE = -1;
    static final int TITLE_STYLE = 4;
    
//    boolean canChangeStyle();
    
    void increase();
    
    void decrease();
    
    boolean canIncrease();
    
    boolean canDecrease();
    
    Font getFont();
    
	Font getFont(int style);
    
    String getName();
    
    int getSize();
    
    int getStyle();
    
    void setFont(Font newFont);
    
    void setFont(String typeName, int size, int style);
//    
//    void setName(String newName);
    
    void setSize(int newSize);
//
//    void setStyle(int newStyle);

    void addFontChangeListener(IFontChangeListener listener);
    void removeFontChangeListener(IFontChangeListener listener);
    void fireFontChanged();

}
