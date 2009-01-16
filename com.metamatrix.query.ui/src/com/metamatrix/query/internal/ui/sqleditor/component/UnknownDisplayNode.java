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

/**
 * The <code>UnknownDisplayNode</code> class is a type of <code>TextDisplayNode</code> that is used to represent an unknown
 * node.
 */
public class UnknownDisplayNode extends TextDisplayNode {

    // ===========================================================================================================================
    // Constructors

    /**
     * @see TextDisplayNode#TextDisplayNode(DisplayNode, String)
     */
    public UnknownDisplayNode(DisplayNode parentNode,
                              String text) {
        super(parentNode, text);
    }
}
