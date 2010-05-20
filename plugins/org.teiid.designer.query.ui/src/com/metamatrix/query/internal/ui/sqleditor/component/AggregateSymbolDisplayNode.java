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
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.Expression;

/**
 * The <code>AggregateSymbolDisplayNode</code> class is used to represent AggregateSymbols.
 */
public class AggregateSymbolDisplayNode extends ExpressionDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   AggregateSymbolDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param symbol the query language object used to construct this display node.
     */
    public AggregateSymbolDisplayNode(DisplayNode parentNode, AggregateSymbol symbol) {
        this.parentNode = parentNode;
        this.languageObject = symbol;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode. The Child node is
     *   the Expression - if there is one.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();
        AggregateSymbol aggregateSymbol = (AggregateSymbol)this.getLanguageObject();
        Expression expr = aggregateSymbol.getExpression();
        if(expr!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expr));
        }
        
        if(aggregateSymbol.getOrderBy() != null ) {
        	childNodeList.add(DisplayNodeFactory.createDisplayNode(this, aggregateSymbol.getOrderBy()));
        }
        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        AggregateSymbol aggregateSymbol = (AggregateSymbol)this.getLanguageObject();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,aggregateSymbol.getAggregateFunction()));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));

        if(aggregateSymbol.isDistinct()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.DISTINCT));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }

        if(childNodeList.size()>0) {
            DisplayNode child = (DisplayNode)childNodeList.get(0);
//            indent = child.getIndentLevel();
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
            
            if(childNodeList.size()>1) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                child = (DisplayNode)childNodeList.get(1);
//              indent = child.getIndentLevel();
              if( child.hasDisplayNodes() ) {
                      displayNodeList.addAll(child.getDisplayNodeList());
              } else {
                      displayNodeList.add(child);
              }
          }
        } else {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ALL_COLS));
        }

        
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
    }

}

