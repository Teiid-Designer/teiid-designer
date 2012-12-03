/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import org.teiid.designer.query.sql.lang.IMatchCriteria;

/**
 * The <code>MatchCriteriaDisplayNode</code> class is used to represent a Match Criteria.
 *
 * @since 8.0
 */
public class MatchCriteriaDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * MatchCriteriaDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param criteria the query language object used to construct this display node.
     */
    public MatchCriteriaDisplayNode( DisplayNode parentNode,
                                     IMatchCriteria criteria ) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean supportsCriteria() {
        return true;
    }

}
