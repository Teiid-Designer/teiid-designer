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
import com.metamatrix.query.sql.proc.AssignmentStatement;
import com.metamatrix.query.sql.proc.RaiseErrorStatement;
import com.metamatrix.query.sql.symbol.Constant;

/**
 * The <code>AssignmentStatementDisplayNode</code> class is used to represent AssignmentStatements.
 */
public class AssignmentStatementDisplayNode extends StatementDisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  AssignmentStatementDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param statement The AssignmentStatement language object used to construct this display node.
     */
    public AssignmentStatementDisplayNode(DisplayNode parentNode, AssignmentStatement statement) {
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
    	AssignmentStatement statement = (AssignmentStatement)getLanguageObject();
        // Add the Element Symbol Child
        if (this.languageObject instanceof RaiseErrorStatement) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ERROR));
        } else {
        	Object var = statement.getVariable();
        	if(var!=null) {
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,var));
        	}
        }
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
        if ( !(this.languageObject instanceof RaiseErrorStatement) &&
        	  ((AssignmentStatement)this.languageObject).getValue()!=null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,EQUALS));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        
        // Add the Command or Expression Child
        if(statement.getValue() != null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,statement.getValue()));
        } else {
            if( statement.getVariable().getType() != null ) {
            	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,new Constant(null, statement.getVariable().getType())));
            }
        }
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SEMICOLON));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));
    }

}
