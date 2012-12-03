/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import org.teiid.designer.query.sql.lang.ICriteria;

/**
 * The <code>HavingDisplayNode</code> class is used to represent a Query's HAVING clause.
 *
 * @since 8.0
 */
public class HavingDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * HavingDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param criteria the query language object used to construct this display node.
     */
    public HavingDisplayNode( DisplayNode parentNode,
                              ICriteria criteria ) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Having Clause supports Criteria
     */
    @Override
    public boolean supportsCriteria() {
        return true;
    }

    /**
     * Having Clause supports Expressions
     */
    @Override
    public boolean supportsExpression() {
        return true;
    }

    /**
     * Having Clause supports Elements
     */
    @Override
    public boolean supportsElement() {
        return true;
    }

}
