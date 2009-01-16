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
