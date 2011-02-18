/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
