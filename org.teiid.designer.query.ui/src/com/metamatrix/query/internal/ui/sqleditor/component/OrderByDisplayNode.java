/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Iterator;
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.OrderBy;
import com.metamatrix.query.sql.symbol.AliasSymbol;
import com.metamatrix.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.query.sql.visitor.SQLStringVisitor;

/**
 * The <code>OrderByDisplayNode</code> class is used to represent a Query's ORDERBY clause.
 */
public class OrderByDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * OrderByDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param orderBy the query language object used to construct this display node.
     */
    public OrderByDisplayNode( DisplayNode parentNode,
                               OrderBy orderBy ) {
        this.parentNode = parentNode;
        this.languageObject = orderBy;
        createChildNodes();
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * OrderBy Clause supports Elements
     */
    @Override
    public boolean supportsElement() {
        return true;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();

        OrderBy orderBy = (OrderBy)(this.getLanguageObject());
        int nVariables = orderBy.getVariableCount();

        for (int i = 0; i < nVariables; i++) {
            SingleElementSymbol symbol = orderBy.getVariable(i);
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, symbol));
        }

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     * Create the DisplayNode list for this type of DisplayNode. This is a list of all the lowest level nodes for this
     * DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();

        OrderBy orderBy = (OrderBy)(this.getLanguageObject());
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.ORDER));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.BY));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));

        if (childNodeList.size() > 0) {
            Iterator iter = childNodeList.iterator();
            Iterator typeIter = orderBy.getTypes().iterator();

            while (iter.hasNext()) {
                DisplayNode childNode = (DisplayNode)iter.next();
                SingleElementSymbol seSymbol = (SingleElementSymbol)childNode.getLanguageObject();
                if (seSymbol instanceof AliasSymbol) {
                    AliasSymbol as = (AliasSymbol)seSymbol;
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,
                                                                             SQLStringVisitor.escapeSinglePart(as.getOutputName())));
                } else {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, seSymbol.getName()));
                }
                Boolean type = (Boolean)typeIter.next();
                if (type.booleanValue() == OrderBy.DESC) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.DESC));
                }

                if (iter.hasNext()) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, COMMA + SPACE));
                }
            }

            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        }
    }

}
