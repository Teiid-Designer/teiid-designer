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
import com.metamatrix.query.sql.proc.DeclareStatement;

/**
 * The <code>DeclareStatementDisplayNode</code> class is used to represent 
 * a DeclareStatement LanguageObject.
 */
public class DeclareStatementDisplayNode extends StatementDisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  DeclareStatementDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param statement The DeclareStatement language object used to construct this display node.
     */
    public DeclareStatementDisplayNode(DisplayNode parentNode, DeclareStatement statement) {
        this.parentNode = parentNode;
        this.languageObject = statement;
    	createDisplayNodeList();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
        DeclareStatement statement = (DeclareStatement)getLanguageObject();
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.DECLARE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,statement.getVariableType()));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
        Object var = statement.getVariable();
        if(var!=null) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,var));
        }

        if(statement.getValue() != null) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,EQUALS));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,statement.getValue()));
        }
    	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SEMICOLON));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));
	}    

}
    
