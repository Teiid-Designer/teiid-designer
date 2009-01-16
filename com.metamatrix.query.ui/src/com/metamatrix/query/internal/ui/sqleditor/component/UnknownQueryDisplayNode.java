/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.*;

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

