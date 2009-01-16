/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
    
