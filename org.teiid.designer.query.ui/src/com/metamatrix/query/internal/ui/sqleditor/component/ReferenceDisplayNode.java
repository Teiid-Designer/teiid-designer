/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import com.metamatrix.query.sql.symbol.Reference;

/**
 * The <code>ReferenceDisplayNode</code> class is used to represent a Reference.
 */
public class ReferenceDisplayNode extends ExpressionDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   FunctionDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param function the query language object used to construct this display node.
     */
    public ReferenceDisplayNode(DisplayNode parentNode, Reference reference) {
        this.parentNode = parentNode;
        this.languageObject = reference;
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
        //int indent = this.getIndentLevel();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,"?")); //$NON-NLS-1$
    }

}

