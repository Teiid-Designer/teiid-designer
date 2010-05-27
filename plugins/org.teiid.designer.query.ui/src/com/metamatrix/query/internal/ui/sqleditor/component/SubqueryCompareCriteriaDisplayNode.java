/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * The <code>CompareCriteriaDisplayNode</code> class is used to represent CompareCriteria.
 */
public class SubqueryCompareCriteriaDisplayNode extends PredicateCriteriaDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   SubqueryCompareCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public SubqueryCompareCriteriaDisplayNode(DisplayNode parentNode, SubqueryCompareCriteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
        createChildNodes();
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

		// First child is the Left Expression
        SubqueryCompareCriteria compareCriteria = (SubqueryCompareCriteria)this.getLanguageObject();
        Expression expression = compareCriteria.getLeftExpression();
        if(expression==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,"ERROR")); //$NON-NLS-1$
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expression));
        }

		// Second child is Command
        Command command = compareCriteria.getCommand();
        if(command==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,"ERROR")); //$NON-NLS-1$
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,command));
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

        SubqueryCompareCriteria compareCriteria = (SubqueryCompareCriteria)this.getLanguageObject();

		// LHS - Expression
		// ----------------------------------------------------------------
        // If Left Expression has display nodes, add them to the list
        // ----------------------------------------------------------------
        DisplayNode child = childNodeList.get(0);
//        int indent = child.getIndentLevel();
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }

		String operator = compareCriteria.getOperatorAsString();
		String quantifier = compareCriteria.getPredicateQuantifierAsString();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,operator));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,quantifier));
		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));

		// RHS - Command
		// ----------------------------------------------------------------
		// If Command has display nodes, add them to the list
		// ----------------------------------------------------------------
        child = childNodeList.get(1);
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }

		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
	}

}
