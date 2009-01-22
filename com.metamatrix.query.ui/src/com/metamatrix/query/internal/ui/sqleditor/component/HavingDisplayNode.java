/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.Criteria;

/**
 * The <code>HavingDisplayNode</code> class is used to represent a Query's HAVING clause.
 */
public class HavingDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   HavingDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public HavingDisplayNode(DisplayNode parentNode, Criteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

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

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();
//        if(isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//        	indent++;
//        }

        Criteria criteria = (Criteria)this.getLanguageObject();
        if(criteria!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,criteria));
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
//        int indent = this.getIndentLevel();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.HAVING));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
//        if(isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//        }

        if(childNodeList.size()>0) {
            DisplayNode childNode = (DisplayNode) childNodeList.get(0);
            //indent = childNode.getIndentLevel();
            if(childNode.hasDisplayNodes()) {
                displayNodeList.addAll(childNode.getDisplayNodeList());
            } else {
                displayNodeList.add(childNode);
            }
//            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
//            if(isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//            	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//            }
        }
	}

}

