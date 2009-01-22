/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.Criteria;

/**
 * The <code>WhereDisplayNode</code> class is used to represent a Query's WHERE clause.
 */
public class WhereDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   WhereDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public WhereDisplayNode(DisplayNode parentNode, Criteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get the Where clause criteria
     */
    @Override
    public CriteriaDisplayNode getCriteria() {
        if(childNodeList.size()!=0) {
            return (CriteriaDisplayNode)childNodeList.get(0);
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

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {

        childNodeList = new ArrayList();
        int indent = this.getIndentLevel();
        if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	indent++;
        }

        Criteria criteria = (Criteria)(this.getLanguageObject());
        if(criteria!=null) {
            DisplayNode dnNode = DisplayNodeFactory.createDisplayNode(this,criteria);
            dnNode.setIndentLevel( indent );            
            childNodeList.add( dnNode );
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
        int childIndent = this.getIndentLevel();
        childIndent++;

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.WHERE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        }

        // process children
        Iterator iter = this.getChildren().iterator();
        
        while(iter.hasNext()) {
            DisplayNode childNode = (DisplayNode) iter.next();            
            
            if(childNode.hasDisplayNodes()) {

                if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                    displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, childIndent ) );
                }
                List lstChildren = childNode.getDisplayNodeList();                
                DisplayNodeUtils.setIndentLevel( lstChildren, childIndent );                
                displayNodeList.addAll(lstChildren);
            } else {
                childNode.setIndentLevel( childIndent );
                if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                    displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, childIndent ) );
                }
                displayNodeList.add(childNode);
            }
            
            if(iter.hasNext()) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            }
        }

//        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
//        if(DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//        }
	}

}

