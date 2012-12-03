/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import org.teiid.designer.query.sql.symbol.IElementSymbol;

/**
 * The <code>ElementSymbolDisplayNode</code> class is used to represent an ElemenSymbol.
 *
 * @since 8.0
 */
public class ElementSymbolDisplayNode extends DisplayNode {

    // ===========================================================================================================================
    // Constructors

    /**
     * ElementSymbolDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param eSymbol the IElementSymbol language object used to construct this display node.
     */
    public ElementSymbolDisplayNode( DisplayNode parentNode,
                                     IElementSymbol eSymbol ) {
        this.parentNode = parentNode;
        this.languageObject = eSymbol;
    }

    // ===========================================================================================================================
    // Methods

    /**
     * Method to set the starting index.
     */
    @Override
    public int setStartIndex( int index ) {
        startIndex = index;
        endIndex = startIndex + toString().length() - 1;
        return endIndex;
    }

    /**
     * @see org.teiid.query.ui.sqleditor.component.DisplayNode#toDisplayString()
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
        IElementSymbol symbol = (IElementSymbol)this.languageObject;
        return symbol.toString();
    }
}
