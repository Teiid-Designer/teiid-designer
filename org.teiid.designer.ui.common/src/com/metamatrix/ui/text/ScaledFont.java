/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.text;

import org.eclipse.swt.graphics.Font;

/**
 * ScaledFont
 */
public interface ScaledFont {
       
    void increase();
    
    void decrease();
    
    boolean canIncrease();
    
    boolean canDecrease();
    
    Font getFont();
    
    String getName();
    
    int getSize();
    
    int getStyle();
    
    void setFont(Font newFont);
    
    void setFont(String typeName, int size, int style); 
    
    void setSize(int newSize);

    void addFontChangeListener(IFontChangeListener listener);

    void removeFontChangeListener(IFontChangeListener listener);

    void fireFontChanged();

}
