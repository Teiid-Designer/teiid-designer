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
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.visitor.SQLStringVisitor;

/**
 * The <code>AliasSymbolDisplayNode</code> class is used to represent AliasSymbols.
 */
public class AliasSymbolDisplayNode extends ExpressionDisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * AliasSymbolDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param symbol the query language object used to construct this display node.
     */
    public AliasSymbolDisplayNode( DisplayNode parentNode,
                                   AliasSymbol symbol ) {
        this.parentNode = parentNode;
        this.languageObject = symbol;
        createChildNodes();
    }

    // /////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the child nodes for this type of DisplayNode. The Child node is the ElementSymbol.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        // int indent = this.getIndentLevel();

        AliasSymbol aliasSymbol = (AliasSymbol)this.getLanguageObject();
        childNodeList.add(DisplayNodeFactory.createDisplayNode(this, aliasSymbol.getSymbol()));

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     * Create the DisplayNode list for this type of DisplayNode. This is a list of all the lowest level nodes for this
     * DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();

        AliasSymbol aliasSymbol = (AliasSymbol)this.getLanguageObject();
        // Get the childNode
        DisplayNode child = childNodeList.get(0);
        // int indent = child.getIndentLevel();

        if (child.hasDisplayNodes()) {
            displayNodeList.addAll(child.getDisplayNodeList());
        } else {
            displayNodeList.add(child);
        }
        // Keyword AS
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SQLConstants.Reserved.AS));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));

        // If alias is reserved word, it needs to be in quotes to escape it
        String alias = SQLStringVisitor.escapeSinglePart(aliasSymbol.getOutputName());
        displayNodeList.add(new AliasNameDisplayNode(this, alias));

    }

    private class AliasNameDisplayNode extends DisplayNode {
        private String name;

        public AliasNameDisplayNode( AliasSymbolDisplayNode theParent,
                                     String theName ) {
            parentNode = theParent;
            name = theName;
            childNodeList = new ArrayList();
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, name));
            displayNodeList = new ArrayList();
            displayNodeList.add(childNodeList.get(0));
        }

        @Override
        public boolean isInExpression() {
            return true;
        }

    }

}
