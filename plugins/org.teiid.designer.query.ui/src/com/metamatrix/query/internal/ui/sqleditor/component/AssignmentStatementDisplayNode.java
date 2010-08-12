/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;

import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ScalarSubquery;

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
    	Object var = statement.getVariable();
    	if(var!=null) {
    		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,var));
    	}
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
	    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,EQUALS));
	    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
        // Add the Command or Expression Child
        if(statement.getExpression() != null) {
        	if (statement.getExpression() instanceof ScalarSubquery) {
            	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,((ScalarSubquery)statement.getExpression()).getCommand()));
        	} else {
            	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,statement.getExpression()));
        	}
        } else {
            if( statement.getVariable().getType() != null ) {
            	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,new Constant(null, statement.getVariable().getType())));
            }
        }
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SEMICOLON));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, CR));
    }

}
