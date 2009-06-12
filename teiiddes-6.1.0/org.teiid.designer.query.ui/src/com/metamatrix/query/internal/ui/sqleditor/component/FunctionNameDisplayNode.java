/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

/**
 * The <code>FunctionNameDisplayNode</code> class is used to represent a Function Name.
 */
public class FunctionNameDisplayNode extends TextDisplayNode {

    // ===========================================================================================================================
    // Constructors

    /**
     * @see TextDisplayNode#TextDisplayNode(DisplayNode, String)
     */
    public FunctionNameDisplayNode(DisplayNode parentNode,
                                   String name) {
        super(parentNode, name);
    }
}
