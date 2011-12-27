/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;

/**
 * The <code>UnknownQueryDisplayNode</code> class is used to represent an Unknown SQL Statement.
 * This class will simply take a String Query and tokenize it, creating DisplayNodes
 * for each String Token.
 */
public class UnknownQueryDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  UnknownQueryDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param sqlString the unparsable SQL statement String.
     */
    public UnknownQueryDisplayNode(DisplayNode parentNode, String sqlString) {
        this.parentNode = parentNode;
        this.languageObject = null;
        createDisplayNodeList(sqlString);
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList(String sqlString) {
        displayNodeList = new ArrayList(1);
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(parentNode,sqlString));
	}

}

