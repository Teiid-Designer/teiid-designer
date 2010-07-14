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
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.LoopStatement;

/**
 * The <code>LoopStatementDisplayNode</code> class is used to represent 
 * an LoopStatement LanguageObject.
 */
public class LoopStatementDisplayNode extends StatementDisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  LoopStatementDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param statement The LoopStatement language object used to construct this display node.
     */
    public LoopStatementDisplayNode(DisplayNode parentNode, LoopStatement statement) {
        this.parentNode = parentNode;
        this.languageObject = statement;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a LoopStatementDisplayNode,
     *  there are two children - (1) The statement command, (2) the Block.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        LoopStatement statement = (LoopStatement)this.getLanguageObject();
//        int indent = this.getIndentLevel();

        //----------------------------------------------------
        // statement Command
        //----------------------------------------------------
        Command command = statement.getCommand();
        if(command!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,command));
        }

        //----------------------------------------------------
        // statement Block
        //----------------------------------------------------
        Block block = statement.getBlock();
        if(block!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,block));
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
        LoopStatement statement = (LoopStatement)this.getLanguageObject();
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.LOOP));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.ON));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
        
        // Add the Command
        DisplayNode node = childNodeList.get(0);
        if(node.hasDisplayNodes()) {
        	displayNodeList.addAll(node.getDisplayNodeList());
        } else {
        	displayNodeList.add(node);
        }
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.AS));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,statement.getCursorName()));
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));

        // Add the Block 
		node = childNodeList.get(1);
        if(node.hasDisplayNodes()) {
        	displayNodeList.addAll(node.getDisplayNodeList());
        } else {
        	displayNodeList.add(node);
        }
	}    

}
