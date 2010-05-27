/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * The <code>CompareCriteriaDisplayNode</code> class is used to represent CompareCriteria.
 */
public class CompareCriteriaDisplayNode extends PredicateCriteriaDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   CompareCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public CompareCriteriaDisplayNode(DisplayNode parentNode, CompareCriteria criteria) {
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

        CompareCriteria compareCriteria = (CompareCriteria)this.getLanguageObject();
        Expression expression = compareCriteria.getLeftExpression();
        if(expression==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,UNDEFINED));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expression));
        }

        expression = compareCriteria.getRightExpression();
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
        displayNodeList = new ArrayList();

        CompareCriteria compareCriteria = (CompareCriteria)this.getLanguageObject();
        // If Left Expression has display nodes, add them to the list
        DisplayNode child = childNodeList.get(0);
        //int indent = child.getIndentLevel();
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,compareCriteria.getOperatorAsString()));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        // If Right Expression has display nodes, add them to the list
        child = childNodeList.get(1);
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }
	}

}

