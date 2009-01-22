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
import com.metamatrix.query.sql.proc.Block;
import com.metamatrix.query.sql.proc.IfStatement;

/**
 * The <code>IfStatementDisplayNode</code> class is used to represent 
 * an IfStatement LanguageObject.
 */
public class IfStatementDisplayNode extends StatementDisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  IfStatementDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param statement The IfStatement language object used to construct this display node.
     */
    public IfStatementDisplayNode(DisplayNode parentNode, IfStatement statement) {
        this.parentNode = parentNode;
        this.languageObject = statement;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For an IfStatementDisplayNode,
     *  there are possibly three children - (1) The statement condition, (2) the If Block, and 
     *  possibly the (3) the else block.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        IfStatement statement = (IfStatement)this.getLanguageObject();
//        int indent = this.getIndentLevel();

        //----------------------------------------------------
        // IfStatement Conditional Criteria
        //----------------------------------------------------
        Criteria criteria = statement.getCondition();
        if(criteria!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,criteria));
        }

        //----------------------------------------------------
        // IfStatement If Block
        //----------------------------------------------------
        Block ifBlock = statement.getIfBlock();
        if(ifBlock!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,ifBlock));
        }

        //----------------------------------------------------
        // IfStatement Else Block
        //----------------------------------------------------
        if(statement.hasElseBlock()) {
        	Block elseBlock = statement.getElseBlock();
	        if(elseBlock!=null) {
	            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,elseBlock));
	        }
        }
        
        //----------------------------------------------------
        // Create the Display Node List
        //----------------------------------------------------
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
        IfStatement statement = (IfStatement)this.getLanguageObject();
        
        //int indent = this.getIndentLevel();
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.IF));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
        // Add the Conditional Criteria
        DisplayNode node = (DisplayNode)childNodeList.get(0);
        if(node.hasDisplayNodes()) {
        	displayNodeList.addAll(node.getDisplayNodeList());
        } else {
        	displayNodeList.add(node);
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));

        // Add the IfBlock 
		node = (DisplayNode)childNodeList.get(1);
        if(node.hasDisplayNodes()) {
        	displayNodeList.addAll(node.getDisplayNodeList());
        } else {
        	displayNodeList.add(node);
        }

        // Add the ElseBlock, if it exists 
        if(statement.hasElseBlock()) {
        	// previous block ended with cr
        	//displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ELSE));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
			node = (DisplayNode)childNodeList.get(2);
	        if(node.hasDisplayNodes()) {
	        	displayNodeList.addAll(node.getDisplayNodeList());
	        } else {
	        	displayNodeList.add(node);
	        }
        }
	}    

}
