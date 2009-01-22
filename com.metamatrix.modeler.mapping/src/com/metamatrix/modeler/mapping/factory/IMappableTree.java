/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.factory;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * IMappableTree is an interface for obtaining the tree structure of mappable model objects.
 */
public interface IMappableTree {

    /**
     * Return this tree's TreeRoot, the upper most non-mappable object in the tree.
     * @return
     */
    EObject getTreeRoot();
    
    /**
     * Return the parent of the specified node.
     * @param node
     * @return the specified node's parent.  If the parent of the specified root is the TreeRoot, 
     * the implementation may return either null or the TreeRoot.  If the specified node is the
     * TreeRoot, the implementation must return null;
     */
    EObject getParent(EObject node);

    /**
     * Return the children of the specified node that should be considered for possible mapping
     * nodes.
     * @param node
     * @return an ordered List of the children beneath the specified node.
     */
    Collection getChildren(EObject node);
 
    /**
     * Determine whether the specified node is a child of the given parent node.
     * @return true if the node is a child of the given parent node.
     */
    boolean isParentOf(EObject parent, EObject child);

    /**
     * Determine whether the specified node is a descendent of the given ancestor node.
     * @return true if the node is a descendent of the given ancestor node.
     */
    boolean isAncestorOf(EObject ancestor, EObject descendent);
 
    /**
     * Determine whether the specified nodes are siblings.
     * @param instance
     * @param possibleSibling
     * @param higherOrderOnly if true, this method should return true only if the objects
     * are siblings AND the possible sibling is ABOVE the instance.
     * @return
     */
    boolean isSiblingOf(EObject instance, EObject possibleSibling, boolean higherOrderOnly); 
 
    /**
     * Determine if the specified node is not actually owned by the tree root of this class.
     * An example would be a node that is owned by a fragment that is used by this tree.
     * @param node
     * @return
     */
    boolean isExternal(EObject node);
    
    /**
     * Return a list of the all external fragment root nodes referenced by this tree, including
     * @param recurseFragments true if this list should recurse down fragments to find other
     * fragment roots.  If false, the list will contain only external roots directly referenced
     * by this tree.
     * @return an ordered List of fragment roots referenced within this tree.
     */
    List getExternalRoots(boolean recurseFragments);
    
    /**
     * Obtain the Datatype of the specified node
     * @param node
     * @return
     */
    EObject getDatatype(EObject node);
    
    /**
     * Determine if the two specified objects are equivalent, such that they could be mapped
     * into the same MappingClassColumn.
     * @param objA
     * @param objB
     * @return
     */
    boolean areEquivalent(EObject objA, EObject objB);
    
    /**
     * Return a qualified name for the specified node.  Usually appends a parent node onto the
     * name.  This method is used to resolve name clashes between mapping class columns with 
     * the same simple name.
     * @param node
     * @return
     */
    String getUniqueName(EObject node);
    
    /**
     * Determine if the specified node is a Choice node
     * @param node
     * @return
     */
    boolean isChoiceNode(EObject node);
    
}
