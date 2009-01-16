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

import com.metamatrix.query.sql.symbol.ElementSymbol;

/**
 * The <code>ElementSymbolDisplayNode</code> class is used to represent an ElemenSymbol.
 */
public class ElementSymbolDisplayNode extends ExpressionDisplayNode {

    // ===========================================================================================================================
    // Constructors

    /**
     * ElementSymbolDisplayNode constructor
     * 
     * @param parentNode
     *            the parent DisplayNode of this.
     * @param eSymbol
     *            the ElementSymbol language object used to construct this display node.
     */
    public ElementSymbolDisplayNode(DisplayNode parentNode,
                                    ElementSymbol eSymbol) {
        this.parentNode = parentNode;
        this.languageObject = eSymbol;
    }

    // ===========================================================================================================================
    // Methods

    /**
     * Method to set the starting index.
     */
    @Override
    public int setStartIndex(int index) {
        startIndex = index;
        endIndex = startIndex + toString().length() - 1;
        return endIndex;
    }

    /**
     * @see com.metamatrix.query.internal.ui.sqleditor.component.DisplayNode#toDisplayString()
     * @since 5.0.1
     */
    @Override
    public String toDisplayString() {
        return (isVisible() ? toString() : BLANK);
    }

    /**
     * TextDisplayNode toString method
     */
    @Override
    public String toString() {
        ElementSymbol symbol = (ElementSymbol)this.languageObject;
        return symbol.toString();
    }
}
