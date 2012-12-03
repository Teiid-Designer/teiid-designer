/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import org.teiid.designer.query.sql.symbol.IAliasSymbol;

/**
 * The <code>AliasSymbolDisplayNode</code> class is used to represent AliasSymbols.
 *
 * @since 8.0
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
                                   IAliasSymbol symbol ) {
        this.parentNode = parentNode;
        this.languageObject = symbol;
    }

}
