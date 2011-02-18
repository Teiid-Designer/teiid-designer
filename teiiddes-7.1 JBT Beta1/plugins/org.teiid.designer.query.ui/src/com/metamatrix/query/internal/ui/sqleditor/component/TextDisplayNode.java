/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
