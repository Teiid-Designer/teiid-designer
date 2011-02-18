/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import org.teiid.query.sql.LanguageObject;

/**
 * The <code>SymbolDisplayNode</code> class is used to represent a Symbol.
 */
public class SymbolDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   SymbolDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param obj the query language object used to construct this display node.
     */
    public SymbolDisplayNode(DisplayNode parentNode, LanguageObject obj) {
        this.parentNode = parentNode;
        this.languageObject = obj;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Method to set the starting index.
     */
    @Override
    public int setStartIndex( int index ) {
        startIndex = index;
        String str = this.getLanguageObject().toString();
        if(str!=null) {
            endIndex = startIndex + str.length() - 1;
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
        return toString();
    }

    @Override
    public String toString() {
        return this.getLanguageObject().toString();
    }

}

