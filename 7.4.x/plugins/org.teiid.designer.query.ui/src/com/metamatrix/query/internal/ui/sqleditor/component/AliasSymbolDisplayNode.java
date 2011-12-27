/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import org.teiid.query.sql.symbol.AliasSymbol;

/**
 * The <code>AliasSymbolDisplayNode</code> class is used to represent AliasSymbols.
 */
public class AliasSymbolDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * AliasSymbolDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param symbol the query language object used to construct this display node.
     */
    public AliasSymbolDisplayNode( DisplayNode parentNode,
                                   AliasSymbol symbol ) {
        this.parentNode = parentNode;
        this.languageObject = symbol;
    }

}
