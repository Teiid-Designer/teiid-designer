/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import org.teiid.query.sql.symbol.ElementSymbol;

/**
 * The <code>ElementSymbolDisplayNode</code> class is used to represent an ElemenSymbol.
 */
public class ElementSymbolDisplayNode extends ExpressionDisplayNode {

    // ===========================================================================================================================
    // Constructors

    /**
     * ElementSymbolDisplayNode constructor
     * 
     * @param parentNode
     *            the parent DisplayNode of this.
     * @param eSymbol
     *            the ElementSymbol language object used to construct this display node.
     */
    public ElementSymbolDisplayNode(DisplayNode parentNode,
                                    ElementSymbol eSymbol) {
        this.parentNode = parentNode;
        this.languageObject = eSymbol;
    }

    // ===========================================================================================================================
    // Methods

    /**
     * Method to set the starting index.
     */
    @Override
    public int setStartIndex(int index) {
        startIndex = index;
        endIndex = startIndex + toString().length() - 1;
        return endIndex;
    }

    /**
     * @see com.metamatrix.query.internal.ui.sqleditor.component.DisplayNode#toDisplayString()
     * @since 5.0.1
     */
    @Override
    public String toDisplayString() {
        return (isVisible() ? toString() : BLANK);
    }

    /**
     * TextDisplayNode toString method
     */
    @Override
    public String toString() {
        ElementSymbol symbol = (ElementSymbol)this.languageObject;
        return symbol.toString();
    }
}
