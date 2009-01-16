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

package com.metamatrix.modeler.mapping.factory;

import java.util.Iterator;

import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.ecore.EObject;

/**
 * MappableTreeIterator is an extension of AbstractTreeIterator that works with IMappableTree.
 */
public class MappableTreeIterator extends AbstractTreeIterator {

    /**
     */
    private static final long serialVersionUID = 1L;
    private IMappableTree tree;

    /**
     * Construct an instance of MappableTreeIterator.
     */
    public MappableTreeIterator(IMappableTree tree) {
        super(tree.getTreeRoot());
        this.tree = tree;
    }
    
    /**
     * Construct an instance of MappableTreeIterator starting at the specified node.
     */
    public MappableTreeIterator(IMappableTree tree, Object node) {
        super(node);
        this.tree = tree;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.util.AbstractTreeIterator#getChildren(java.lang.Object)
     */
    @Override
    protected Iterator getChildren(Object object) {
        return tree.getChildren((EObject) object).iterator();
    }

}
