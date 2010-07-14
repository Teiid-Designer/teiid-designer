/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;

import org.teiid.language.SQLConstants;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * The <code>IsNullCriteriaDisplayNode</code> class is used to represent an IsNull Criteria.
 */
public class IsNullCriteriaDisplayNode extends PredicateCriteriaDisplayNode {

    private static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   IsNullCriteriaDisplayNode constructors
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public IsNullCriteriaDisplayNode(DisplayNode parentNode, IsNullCriteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        IsNullCriteria isNullCriteria = (IsNullCriteria)this.getLanguageObject();
        Expression expression = isNullCriteria.getExpression();
        if(expression==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,UNDEFINED));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expression));
        }

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        IsNullCriteria isNullCriteria = (IsNullCriteria)this.getLanguageObject();
        displayNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        // If Expression has display nodes, add them to the list
        DisplayNode child = childNodeList.get(0);
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.IS));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        if (isNullCriteria.isNegated()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.NOT));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.NULL));

	}

}

