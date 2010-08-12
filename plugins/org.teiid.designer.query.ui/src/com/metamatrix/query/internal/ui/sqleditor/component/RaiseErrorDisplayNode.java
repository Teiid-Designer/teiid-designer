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
import org.teiid.query.sql.proc.RaiseErrorStatement;

/**
 * The <code>DeclareStatementDisplayNode</code> class is used to represent 
 * a DeclareStatement LanguageObject.
 */
public class RaiseErrorDisplayNode extends StatementDisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  DeclareStatementDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param statement The DeclareStatement language object used to construct this display node.
     */
    public RaiseErrorDisplayNode(DisplayNode parentNode, RaiseErrorStatement statement) {
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
        RaiseErrorStatement statement = (RaiseErrorStatement)getLanguageObject();
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.ERROR));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
    	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,statement.getExpression()));
    	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SEMICOLON));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));
	}    

}
    
