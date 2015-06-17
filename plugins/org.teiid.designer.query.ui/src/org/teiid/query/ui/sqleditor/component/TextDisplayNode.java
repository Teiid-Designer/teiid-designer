/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;


/**
 * The <code>TextDisplayNode</code> class is used to represent text display nodes.
 *
 * @since 8.0
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
     * @see org.teiid.query.ui.sqleditor.component.DisplayNode#toDisplayString()
     * @since 5.0.1
     */
    @Override
    public String toDisplayString() {
        if (! isVisible())
            return BLANK;

        if (textStr.equals("\u0000")) //$NON-NLS-1$
            return "\\u0000"; //$NON-NLS-1$

        return this.textStr;
    }
    
    /**
     * TextDisplayNode toString method
     */
    @Override
    public String toString() {
        return textStr;
    }
}
