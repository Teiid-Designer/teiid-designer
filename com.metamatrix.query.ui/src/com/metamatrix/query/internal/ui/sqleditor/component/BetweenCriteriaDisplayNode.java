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
import com.metamatrix.query.sql.lang.BetweenCriteria;

/**
 * The <code>BetweenCriteriaDisplayNode</code> class is used to represent an
 * BetweenCriteria LanguageObject.
 */
public class BetweenCriteriaDisplayNode extends PredicateCriteriaDisplayNode {
    
    private static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  BetweenCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria The BetweenCriteria language object used to construct this display node.
     */
    public BetweenCriteriaDisplayNode(DisplayNode parentNode, BetweenCriteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For an BetweenCriteriaDisplayNode,
     *  there are three children - (1) Expression, (2) lower Expression, (3) upper Expression
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        BetweenCriteria criteria = (BetweenCriteria)(this.getLanguageObject());

        //----------------------------------------------------
        // Child1 - expression
        //----------------------------------------------------
        if (criteria.getExpression() != null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,criteria.getExpression()));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,UNDEFINED));
        }

        //----------------------------------------------------
        // Child2 - lower expression
        //----------------------------------------------------
        if (criteria.getLowerExpression() != null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,criteria.getLowerExpression()));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,UNDEFINED));
        }

        //----------------------------------------------------
        // Child3 - upper expression
        //----------------------------------------------------
        if (criteria.getUpperExpression() != null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,criteria.getUpperExpression()));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,UNDEFINED));
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
        BetweenCriteria criteria = (BetweenCriteria)(this.getLanguageObject());

        // Add the Expression Child
        DisplayNode node = (DisplayNode)childNodeList.get(0);
        if( node.hasDisplayNodes() ) {
            displayNodeList.addAll(node.getDisplayNodeList());
        } else {
            displayNodeList.add(node);
        }
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        if (criteria.isNegated()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NOT));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.BETWEEN));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        // Add the Lower Expression Child
        node = (DisplayNode)childNodeList.get(1);
        if( node.hasDisplayNodes() ) {
            displayNodeList.addAll(node.getDisplayNodeList());
        } else {
            displayNodeList.add(node);
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.AND));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        // Add the Upper Expression Child
        node = (DisplayNode)childNodeList.get(2);
        if( node.hasDisplayNodes() ) {
            displayNodeList.addAll(node.getDisplayNodeList());
        } else {
            displayNodeList.add(node);
        }

    }
}
