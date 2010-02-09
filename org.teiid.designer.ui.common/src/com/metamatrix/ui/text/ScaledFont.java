/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.text;


/**
 * ScaledFont
 */
public interface ScaledFont {
       
    void increase();
    
    void decrease();
    
    boolean canIncrease();
    
    boolean canDecrease();

    void addFontChangeListener(IFontChangeListener listener);

    void removeFontChangeListener(IFontChangeListener listener);

    void fireFontChanged();

}
