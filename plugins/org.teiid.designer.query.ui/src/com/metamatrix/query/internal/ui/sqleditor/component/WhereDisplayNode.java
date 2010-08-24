/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import org.teiid.query.sql.lang.Criteria;

/**
 * The <code>WhereDisplayNode</code> class is used to represent a Query's WHERE clause.
 */
public class WhereDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * WhereDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param criteria the query language object used to construct this display node.
     */
    public WhereDisplayNode( DisplayNode parentNode,
                             Criteria criteria ) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Get the Where clause criteria
     */
    @Override
    public DisplayNode getCriteria() {
        if (childNodeList.size() != 0) {
            return childNodeList.get(0);
        }
        return null;
    }

    /**
     * Where Clause supports Criteria.
     */
    @Override
    public boolean supportsCriteria() {
        return true;
    }

    /**
     * Where Clause supports Expressions
     */
    @Override
    public boolean supportsExpression() {
        return true;
    }

    /**
     * Where Clause supports Elements
     */
    @Override
    public boolean supportsElement() {
        return true;
    }

}
