/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import org.teiid.designer.query.sql.lang.ISetCriteria;

/**
 * The <code>SetCriteriaDisplayNode</code> class is used to represent a Set Criteria.
 *
 * @since 8.0
 */
public class SetCriteriaDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * SetCriteriaDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param criteria the query language object used to construct this display node.
     */
    public SetCriteriaDisplayNode( DisplayNode parentNode,
                                   ISetCriteria criteria ) {
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
