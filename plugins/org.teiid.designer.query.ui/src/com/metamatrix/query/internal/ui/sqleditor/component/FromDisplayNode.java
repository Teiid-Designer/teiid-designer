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
import com.metamatrix.query.sql.lang.From;
import com.metamatrix.query.sql.lang.FromClause;

/**
 * The <code>FromDisplayNode</code> class is used to represent a Query's entire FROM clause.
 */
public class FromDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   FromDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param from the query language object used to construct this display node.
     */
    public FromDisplayNode(DisplayNode parentNode, From from) {
        this.parentNode = parentNode;
        this.languageObject = from;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * From Clause supports Criteria
     */
    @Override
    public boolean supportsCriteria() {
        return false;
    }

    /**
     * From Clause supports Expressions
     */
    @Override
    public boolean supportsExpression() {
        return true;
    }

    /**
     * From Clause supports Groups
     */
    @Override
    public boolean supportsGroup() {
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
        From from = (From)(this.getLanguageObject());
        List clauses = from.getClauses();

        int indent = this.getIndentLevel();
        if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	indent++;
        }
	    Iterator iter = clauses.iterator();
        while( iter.hasNext() ) {
			FromClause clause = (FromClause) iter.next();
            DisplayNode dnNode = DisplayNodeFactory.createDisplayNode(this,clause);
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

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.FROM));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        }

        // process children
        Iterator iter = this.getChildren().iterator();
        int iChild = 0;        
        while(iter.hasNext()) {
            DisplayNode childNode = (DisplayNode) iter.next();            
            // Only add "indent" nodes to first clause (they are tabs, at least for now)
            if(childNode.hasDisplayNodes()) {

                if(iChild==0 && DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                    displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, childIndent ) );
                }
                List lstChildren = childNode.getDisplayNodeList();                
                DisplayNodeUtils.setIndentLevel( lstChildren, childIndent );                
                displayNodeList.addAll(lstChildren);
            } else {
                childNode.setIndentLevel( childIndent );
                if(iChild==0 && DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                    displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, childIndent ) );
                }
                displayNodeList.add(childNode);
            }
            
            if(iter.hasNext()) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            }
            iChild++;
        }

//        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
//        if(DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//        }
	}

}

