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

import com.metamatrix.query.sql.proc.CommandStatement;

/**
 * The <code>CommandStatementDisplayNode</code> class is used to represent 
 * a CommandStatement LanguageObject.
 */
public class CommandStatementDisplayNode extends StatementDisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  CommandStatementDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param statement The CommandStatement language object used to construct this display node.
     */
    public CommandStatementDisplayNode(DisplayNode parentNode, CommandStatement statement) {
        this.parentNode = parentNode;
        this.languageObject = statement;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a CommandStatementDisplayNode,
     *  there is one child - a Command.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        
        CommandStatement statement = (CommandStatement)this.getLanguageObject();
//        int indent = this.getIndentLevel();

        childNodeList.add(DisplayNodeFactory.createDisplayNode(this,statement.getCommand()));

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
//        int indent = this.getIndentLevel();
 
		// Add the DisplayNodes for the statement's Command
        List children = getChildren();
        DisplayNode node = (DisplayNode)children.get(0);
        List commandDisplayNodes = node.getDisplayNodeList();
        displayNodeList.addAll(commandDisplayNodes);
        
        // Add the terminating semicolon.  Set the indent level equal to the last statement node indent
//        int numberDNs = commandDisplayNodes.size();
//        int indentLevel = indent;  // default
//        // Get the last command node indent
//        if(numberDNs>0) {
//        	indentLevel = ((DisplayNode)commandDisplayNodes.get(numberDNs-1)).getIndentLevel();
//        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SEMICOLON));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));
	}    

}
