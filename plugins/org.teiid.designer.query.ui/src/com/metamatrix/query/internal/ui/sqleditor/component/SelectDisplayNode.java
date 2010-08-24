/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import org.teiid.query.sql.lang.Select;

/**
 * The <code>SelectDisplayNode</code> class is used to represent a Query's SELECT clause.
 */
public class SelectDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * SelectDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param select the query language object used to construct this display node.
     */
    public SelectDisplayNode( DisplayNode parentNode,
                              Select select ) {
        this.parentNode = parentNode;
        this.languageObject = select;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Select Clause supports Expressions
     */
    @Override
    public boolean supportsExpression() {
        return true;
    }

    /**
     * Select Clause supports Elements
     */
    @Override
    public boolean supportsElement() {
        return true;
    }

    public boolean isDescendentOfSetQuery() {
        DisplayNode displayNode = this;

        DisplayNode parentNode = displayNode.getParent();
        while (parentNode != null) {
            //            System.out.println("[SelectDisplayNode.isOutermostSelect] parentNode: " + parentNode.getClass().getName() ); //$NON-NLS-1$ 
            if (parentNode instanceof SetQueryDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }

        return false;
    }

}
