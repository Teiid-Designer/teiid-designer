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
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.lang.SubquerySetCriteria;
import com.metamatrix.query.sql.symbol.Expression;

/**
 * The <code>SubquerySetCriteriaDisplayNode</code> class is used 
 * to represent a Subquery SetCriteria.
 */
public class SubquerySetCriteriaDisplayNode extends CriteriaDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   SubquerySetCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param setCriteria the language object used to construct this display node.
     */
    public SubquerySetCriteriaDisplayNode(DisplayNode parentNode, SubquerySetCriteria setCriteria) {
        this.parentNode = parentNode;
        this.languageObject = setCriteria;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        //int indent = this.getIndentLevel();

		SubquerySetCriteria subQuerySetCriteria = (SubquerySetCriteria)(this.getLanguageObject());
		
		Expression expression = subQuerySetCriteria.getExpression();
		childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expression));
		
        Command command = subQuerySetCriteria.getCommand();
		childNodeList.add(DisplayNodeFactory.createDisplayNode(this,command));

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
		//int indent = this.getIndentLevel();
		
        SubquerySetCriteria subquerySetCriteria = (SubquerySetCriteria)this.getLanguageObject();
        
        // First ChildNode is the Expression
        DisplayNode childNode = (DisplayNode)childNodeList.get(0);
        if(childNode.hasDisplayNodes()) {
            displayNodeList.addAll(childNode.getDisplayNodeList());
        } else {
            displayNodeList.add(childNode);
        }
        
        // Keyword IN
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        if (subquerySetCriteria.isNegated()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NOT));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }                
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.IN));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
        
        // Second ChildNode is the command
        childNode = (DisplayNode)childNodeList.get(1);
        if(childNode.hasDisplayNodes()) {
            displayNodeList.addAll(childNode.getDisplayNodeList());
        } else {
            displayNodeList.add(childNode);
        }
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
	}

}

