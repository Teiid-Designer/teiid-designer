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
import org.teiid.query.sql.symbol.SearchedCaseExpression;

/**
 * SearchedCaseExpressionDisplayNode
 */
public class SearchedCaseExpressionDisplayNode extends ExpressionDisplayNode {

    List whenCriteriaList = new ArrayList();
    List thenExprList = new ArrayList();
        
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  SearchedCaseExpressionDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param expression The SearchedCaseExpression language object used to construct this display node.
     */
    public SearchedCaseExpressionDisplayNode(DisplayNode parentNode, SearchedCaseExpression expression) {
        this.parentNode = parentNode;
        this.languageObject = expression;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a SearchedCaseExpressionDisplayNode,
     *  there are n children - (n) WHEN-THEN Crit/Exprs, (1 or 0) ELSE Expression
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        SearchedCaseExpression caseExpr = (SearchedCaseExpression)(this.getLanguageObject());

        //----------------------------------------------------
        // n Children - WHEN-THEN criteria/expression pairs
        //----------------------------------------------------
        for (int i = 0; i < caseExpr.getWhenCount(); i++) {
            DisplayNode whenNode = DisplayNodeFactory.createDisplayNode(this,caseExpr.getWhenCriteria(i));
            DisplayNode thenNode = DisplayNodeFactory.createDisplayNode(this,caseExpr.getThenExpression(i));
            whenCriteriaList.add(whenNode);
            thenExprList.add(thenNode);
            childNodeList.add(whenNode);
            childNodeList.add(thenNode);
        }
        
        //----------------------------------------------------
        // Child n - ELSE expression
        //----------------------------------------------------
        if (caseExpr.getElseExpression() != null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,caseExpr.getElseExpression()));
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
        SearchedCaseExpression caseExpr = (SearchedCaseExpression)(this.getLanguageObject());

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.CASE));

        //----------------------------------------------------
        // n Children - WHEN-THEN criteria/expression pairs
        //----------------------------------------------------
        for (int i = 0; i < whenCriteriaList.size(); i++) {
            DisplayNode whenNode = (DisplayNode)whenCriteriaList.get(i);
            DisplayNode thenNode = (DisplayNode)thenExprList.get(i);
            
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.WHEN));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            
            // Add the WHEN Criteria Child
            if( whenNode.hasDisplayNodes() ) {
                displayNodeList.addAll(whenNode.getDisplayNodeList());
            } else {
                displayNodeList.add(whenNode);
            }
            
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.THEN));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            
            // Add the THEN Expression Child
            if( thenNode.hasDisplayNodes() ) {
                displayNodeList.addAll(thenNode.getDisplayNodeList());
            } else {
                displayNodeList.add(thenNode);
            }
        }
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        //----------------------------------------------------
        // 0 or 1 Children - ELSE expression
        //----------------------------------------------------
        if (caseExpr.getElseExpression() != null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ELSE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

            int nChildren = childNodeList.size();
            DisplayNode elseNode = childNodeList.get(nChildren-1);
            if( elseNode!=null && elseNode.hasDisplayNodes() ) {
                displayNodeList.addAll(elseNode.getDisplayNodeList());
            } else {
                displayNodeList.add(elseNode);
            }

            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.END));
    }

}
