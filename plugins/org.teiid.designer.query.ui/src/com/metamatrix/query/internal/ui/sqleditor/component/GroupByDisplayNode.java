/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import org.teiid.query.sql.lang.GroupBy;

/**
 * The <code>GroupByDisplayNode</code> class is used to represent a Query's GROUPBY clause.
 */
public class GroupByDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * GroupByDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param groupBy the query language object used to construct this display node.
     */
    public GroupByDisplayNode( DisplayNode parentNode,
                               GroupBy groupBy ) {
        this.parentNode = parentNode;
        this.languageObject = groupBy;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * GroupBy Clause supports Elements
     */
    @Override
    public boolean supportsElement() {
        return true;
    }

}
