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

package com.metamatrix.query.internal.ui.sqleditor.component;

/**
 * The <code>TextDisplayNode</code> class is used to represent text display nodes.
 */
public class TextDisplayNode extends DisplayNode {

    // ===========================================================================================================================
    // Variables
    
    private String textStr;

    // ===========================================================================================================================
    // Constructors

    /**
     * @param parentNode
     *            the parent DisplayNode of this.
     * @param text
     *            the text for this display Node.
     */
    public TextDisplayNode(DisplayNode parentNode,
                           String text) {
        this.parentNode = parentNode;
        this.textStr = text;
    }

    // ===========================================================================================================================
    // Methods

    /**
     * Method to set the starting index.
     */
    @Override
    public int setStartIndex(int index) {
        startIndex = index;
        if (textStr != null && isVisible()) {
            endIndex = startIndex + textStr.length() - 1;
        } else {
            endIndex = startIndex;
        }
        return endIndex;
    }

    /** 
     * @see com.metamatrix.query.internal.ui.sqleditor.component.DisplayNode#toDisplayString()
     * @since 5.0.1
     */
    @Override
    public String toDisplayString() {
        return (isVisible() ? this.textStr : BLANK);
    }
    
    /**
     * TextDisplayNode toString method
     */
    @Override
    public String toString() {
        return textStr;
    }
}
