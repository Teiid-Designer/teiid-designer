/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.List;
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.symbol.CaseExpression;

/**
 * CaseExpressionDisplayNode
 */
public class CaseExpressionDisplayNode extends ExpressionDisplayNode {

    List whenExprList = new ArrayList();
    List thenExprList = new ArrayList();
        
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  CaseExpressionDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param expression The CaseExpression language object used to construct this display node.
     */
    public CaseExpressionDisplayNode(DisplayNode parentNode, CaseExpression expression) {
        this.parentNode = parentNode;
        this.languageObject = expression;
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
        CaseExpression caseExpr = (CaseExpression)(this.getLanguageObject());

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.CASE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        // checking for null compare in decode string case 2969
        for (int i =0; i < caseExpr.getWhenCount(); i++) {          
            if (ReservedWords.NULL.equalsIgnoreCase(caseExpr.getWhenExpression(i).toString() ) ) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.WHEN));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,caseExpr.getExpression()));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.IS));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NULL));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.THEN));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,caseExpr.getThenExpression(i)));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            }
        }
        for (int i = 0; i < caseExpr.getWhenCount(); i++) {
            if(!ReservedWords.NULL.equalsIgnoreCase(caseExpr.getWhenExpression(i).toString() ) ) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.WHEN));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,caseExpr.getExpression()));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,EQUALS));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,caseExpr.getWhenExpression(i)));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.THEN));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,caseExpr.getThenExpression(i)));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            }
        }
	    if (caseExpr.getElseExpression() != null) {
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ELSE));
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,caseExpr.getElseExpression()));
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
	    }
	    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.END));

    }
}
