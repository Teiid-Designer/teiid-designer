/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import org.teiid.designer.query.sql.symbol.IFunction;

/**
 * The <code>FunctionDisplayNode</code> class is used to represent a Function.
 *
 * @since 8.0
 */
public class FunctionDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * FunctionDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param function the query language object used to construct this display node.
     */
    public FunctionDisplayNode( DisplayNode parentNode,
                                IFunction function ) {
        this.parentNode = parentNode;
        this.languageObject = function;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * This supports expressions within it.
     */
    @Override
    public boolean supportsExpression() {
        return true;
    }

}
