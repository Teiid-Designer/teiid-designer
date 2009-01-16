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

import com.metamatrix.query.sql.symbol.GroupSymbol;

/**
 * The <code>GroupSymbolDisplayNode</code> class is used to represent a GroupSymbol.
 */
public class GroupSymbolDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   GroupSymbolDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param obj the query language object used to construct this display node.
     */
    public GroupSymbolDisplayNode(DisplayNode parentNode, GroupSymbol gSymbol) {
        this.parentNode = parentNode;
        this.languageObject = gSymbol;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Method to set the starting index.
     */
    @Override
    public int setStartIndex( int index ) {
        startIndex = index;
        String str = this.getLanguageObject().toString();
        if(str!=null) {
            endIndex = startIndex + str.length() - 1;
        } else {
            endIndex = startIndex;
        }
        return endIndex;
    }
    
    /** 
     * @see com.metamatrix.query.internal.ui.sqleditor.component.DisplayNode#toDisplayString()
     * @since 5.0.1
     */
    @Override
    public String toDisplayString() {
        return toString();
    }

    @Override
    public String toString() {
        return this.getLanguageObject().toString();
    }

}

