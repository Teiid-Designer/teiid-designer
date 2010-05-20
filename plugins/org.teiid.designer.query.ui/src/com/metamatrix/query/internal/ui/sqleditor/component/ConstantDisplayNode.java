/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import org.teiid.query.sql.symbol.Constant;

/**
 * The <code>ConstantDisplayNode</code> class is used to represent Constants.
 */
public class ConstantDisplayNode extends ExpressionDisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * ConstantDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param constant the query language object used to construct this display node.
     */
    public ConstantDisplayNode( DisplayNode parentNode,
                                Constant constant ) {
        this.parentNode = parentNode;
        this.languageObject = constant;
        createDisplayNodeList();
    }

    // /////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////

    public Object getValue() {
        Constant constant = (Constant)this.getLanguageObject();
        return constant.getValue();
    }

    /**
     * Create the DisplayNode list for this type of DisplayNode. This is a list of all the lowest level nodes for this
     * DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();

        Constant constant = (Constant)this.getLanguageObject();
        String str = constant.toString();

        if (str != null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, str));
        }
        return;
    }

}
