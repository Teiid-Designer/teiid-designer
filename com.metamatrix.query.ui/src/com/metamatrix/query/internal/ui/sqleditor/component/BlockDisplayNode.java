/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.proc.Block;
import com.metamatrix.query.sql.proc.Statement;

/**
 * The <code>BlockDisplayNode</code> class is used to represent a Block LanguageObject.
 */
public class BlockDisplayNode extends DisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  BlockDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param block The Block language object used to construct this display node.
     */
    public BlockDisplayNode(DisplayNode parentNode, Block block) {
        this.parentNode = parentNode;
        this.languageObject = block;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a BlockDisplayNode,
     *  the children are Statements.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
		
    	List statements = ((Block)this.getLanguageObject()).getStatements();
    	Iterator iter = statements.iterator();
    	while(iter.hasNext()) {
    		Statement statement = (Statement)iter.next();
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,statement));
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
        int childIndent = (isVisible() ? this.getIndentLevel() + 1 : 0);
        
        List children = getChildren();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.BEGIN));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));
    	if (children.size() == 1) {
        	// Add the Statement
        	DisplayNode childNode = (DisplayNode)children.get(0);
        	List statementNodes = childNode.getDisplayNodeList();
            DisplayNodeUtils.setIndentLevel(statementNodes, childIndent);
            addIndentedNodes(childNode, childIndent);
    	} else if (children.size() > 1) {
            Iterator stmtIter = children.iterator();
            while (stmtIter.hasNext()) {
				// Add each statement
                DisplayNode statementNode = (DisplayNode)stmtIter.next();
                addIndentedNodes(statementNode, childIndent);
            }	            
        } else {
            // Shouldn't happen, but being tolerant
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.END));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));
	}    

}
