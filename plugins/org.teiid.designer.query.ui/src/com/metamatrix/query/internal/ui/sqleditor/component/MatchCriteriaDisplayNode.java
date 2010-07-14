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
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * The <code>MatchCriteriaDisplayNode</code> class is used to represent a Match Criteria.
 */
public class MatchCriteriaDisplayNode extends PredicateCriteriaDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   MatchCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public MatchCriteriaDisplayNode(DisplayNode parentNode, MatchCriteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean supportsCriteria() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        MatchCriteria matchCriteria = (MatchCriteria)this.getLanguageObject();
        Expression expression = matchCriteria.getLeftExpression();
        if(expression==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,ERROR));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expression));
        }

        expression = matchCriteria.getRightExpression();
        if(expression==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,ERROR));
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
        displayNodeList = new ArrayList();
//       int indent = this.getIndentLevel();

        MatchCriteria matchCriteria = (MatchCriteria)this.getLanguageObject();
        // If Left Expression has display nodes, add them to the list
        DisplayNode child = childNodeList.get(0);
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        if (matchCriteria.isNegated()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.NOT));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.LIKE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        // If Right Expression has display nodes, add them to the list
        child = childNodeList.get(1);
//        indent = child.getIndentLevel();
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }

		// escape character if there is one
		if(matchCriteria.getEscapeChar() != MatchCriteria.NULL_ESCAPE_CHAR) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.ESCAPE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this," '"));   //$NON-NLS-1$
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,String.valueOf(matchCriteria.getEscapeChar())));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,"'"));    //$NON-NLS-1$
		}
	}

}

