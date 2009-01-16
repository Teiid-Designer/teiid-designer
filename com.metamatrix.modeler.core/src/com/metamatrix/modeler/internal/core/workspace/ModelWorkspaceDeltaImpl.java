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

package com.metamatrix.modeler.internal.core.workspace;

import java.util.ArrayList;

import org.eclipse.core.resources.IResourceDelta;

import com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;

/**
 * ModelWorkspaceDeltaImpl
 */
public class ModelWorkspaceDeltaImpl implements ModelWorkspaceDelta {

    /**
     * The element that this delta describes the change to.
     * @see #getElement()
     */
    protected ModelWorkspaceItem fChangedElement;
    /**
     * @see #getKind()
     */
    private int fKind = 0;
    /**
     * @see #getFlags()
     */
    private int fChangeFlags = 0;
    /**
     * @see #getAffectedChildren()
     */
    protected ModelWorkspaceDelta[] fAffectedChildren = fgEmptyDelta;

    /**
     * Collection of resource deltas that correspond to non java resources deltas.
     */
    protected IResourceDelta[] resourceDeltas = null;

    /**
     * Counter of resource deltas
     */
    protected int resourceDeltasCounter;
    /**
     * @see #getMovedFromHandle()
     */
    protected ModelWorkspaceItem fMovedFromHandle = null;
    /**
     * @see #getMovedToHandle()
     */
    protected ModelWorkspaceItem fMovedToHandle = null;
    /**
     * Empty array of ModelWorkspaceDelta
     */
    protected static  ModelWorkspaceDelta[] fgEmptyDelta= new ModelWorkspaceDelta[] {};

    /**
     * Construct an instance of ModelWorkspaceDeltaImpl.
     * 
     */
    public ModelWorkspaceDeltaImpl( final ModelWorkspaceItem changedItem ) {
        super();
        this.fChangedElement = changedItem;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getAddedChildren()
     */
    public ModelWorkspaceDelta[] getAddedChildren() {
        return getChildrenOfType(ADDED);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getAffectedChildren()
     */
    public ModelWorkspaceDelta[] getAffectedChildren() {
        return fAffectedChildren;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getChangedChildren()
     */
    public ModelWorkspaceDelta[] getChangedChildren() {
        return getChildrenOfType(CHANGED);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getElement()
     */
    public ModelWorkspaceItem getElement() {
        return this.fChangedElement;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getFlags()
     */
    public int getFlags() {
        return this.fChangeFlags;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getKind()
     */
    public int getKind() {
        return fKind;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getMovedFromElement()
     */
    public ModelWorkspaceItem getMovedFromElement() {
        return fMovedFromHandle;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getMovedToElement()
     */
    public ModelWorkspaceItem getMovedToElement() {
        return fMovedToHandle;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getRemovedChildren()
     */
    public ModelWorkspaceDelta[] getRemovedChildren() {
        return getChildrenOfType(REMOVED);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceDelta#getResourceDeltas()
     */
    public IResourceDelta[] getResourceDeltas() {
        if (resourceDeltas == null) return null;
        if (resourceDeltas.length != resourceDeltasCounter) {
            System.arraycopy(resourceDeltas, 0, resourceDeltas = new IResourceDelta[resourceDeltasCounter], 0, resourceDeltasCounter);
        }
        return resourceDeltas;
    }
    
    /**
     * @see IJavaElementDelta
     */
    protected ModelWorkspaceDelta[] getChildrenOfType(int type) {
        int length = fAffectedChildren.length;
        if (length == 0) {
            return new ModelWorkspaceDelta[] {};
        }
        ArrayList children= new ArrayList(length);
        for (int i = 0; i < length; i++) {
            if (fAffectedChildren[i].getKind() == type) {
                children.add(fAffectedChildren[i]);
            }
        }

        ModelWorkspaceDelta[] childrenOfType = new ModelWorkspaceDelta[children.size()];
        children.toArray(childrenOfType);
    
        return childrenOfType;
    }

    /**
     * Adds the child delta to the collection of affected children.  If the
     * child is already in the collection, walk down the hierarchy.
     */
    protected void addResourceDelta(IResourceDelta child) {
        switch (fKind) {
            case ADDED:
            case REMOVED:
                // no need to add a child if this parent is added or removed
                return;
            case CHANGED:
                fChangeFlags |= F_CONTENT;
                break;
            default:
                fKind = CHANGED;
                fChangeFlags |= F_CONTENT;
        }
        if (resourceDeltas == null) {
            resourceDeltas = new IResourceDelta[5];
            resourceDeltas[resourceDeltasCounter++] = child;
            return;
        }
        if (resourceDeltas.length == resourceDeltasCounter) {
            // need a resize
            System.arraycopy(resourceDeltas, 0, (resourceDeltas = new IResourceDelta[resourceDeltasCounter * 2]), 0, resourceDeltasCounter);
        }
        resourceDeltas[resourceDeltasCounter++] = child;
    }

    /**
     * Creates the delta tree for the given element and delta, and then
     * inserts the tree as an affected child of this node.
     */
    protected void insertDeltaTree( final ModelWorkspaceItem element, final ModelWorkspaceDeltaImpl delta) {
        ModelWorkspaceDeltaImpl childDelta = createDeltaTree(element, delta);
        if (!this.equalsAndSameParent(element, getElement())) { // handle case of two jars that can be equals but not in the same project
            addAffectedChild(childDelta);
        }
    }

    /**
     * Creates the nested delta deltas based on the affected element
     * its delta, and the root of this delta tree. Returns the root
     * of the created delta tree.
     */
    protected ModelWorkspaceDeltaImpl createDeltaTree(ModelWorkspaceItem element, ModelWorkspaceDeltaImpl delta) {
        ModelWorkspaceDeltaImpl childDelta = delta;
        ArrayList ancestors= getAncestors(element);
        if (ancestors == null) {
            if (this.equalsAndSameParent(delta.getElement(), getElement())) { // handle case of two jars that can be equals but not in the same project
                // the element being changed is the root element
                fKind= delta.fKind;
                fChangeFlags = delta.fChangeFlags;
                fMovedToHandle = delta.fMovedToHandle;
                fMovedFromHandle = delta.fMovedFromHandle;
            }
        } else {
            for (int i = 0, size = ancestors.size(); i < size; i++) {
                ModelWorkspaceItem ancestor = (ModelWorkspaceItem) ancestors.get(i);
                ModelWorkspaceDeltaImpl ancestorDelta = new ModelWorkspaceDeltaImpl(ancestor);
                ancestorDelta.addAffectedChild(childDelta);
                childDelta = ancestorDelta;
            }
        }
        return childDelta;
    }

    /**
     * Creates the nested deltas resulting from an move operation.
     * Convenience method for creating the "move from" delta.
     * The constructor should be used to create the root delta 
     * and then the move operation should call this method.
     */
    public void movedFrom(ModelWorkspaceItem movedFromElement, ModelWorkspaceItem movedToElement) {
        ModelWorkspaceDeltaImpl removedDelta = new ModelWorkspaceDeltaImpl(movedFromElement);
        removedDelta.fKind = REMOVED;
        removedDelta.fChangeFlags |= F_MOVED_TO;
        removedDelta.fMovedToHandle = movedToElement;
        insertDeltaTree(movedFromElement, removedDelta);
    }
    /**
     * Creates the nested deltas resulting from an move operation.
     * Convenience method for creating the "move to" delta.
     * The constructor should be used to create the root delta 
     * and then the move operation should call this method.
     */
    public void movedTo(ModelWorkspaceItem movedToElement, ModelWorkspaceItem movedFromElement) {
        ModelWorkspaceDeltaImpl addedDelta = new ModelWorkspaceDeltaImpl(movedToElement);
        addedDelta.fKind = ADDED;
        addedDelta.fChangeFlags |= F_MOVED_FROM;
        addedDelta.fMovedFromHandle = movedFromElement;
        insertDeltaTree(movedToElement, addedDelta);
    }

    /**
     * Returns a collection of all the parents of this element up to (but
     * not including) the root of this tree in bottom-up order. If the given
     * element is not a descendant of the root of this tree, <code>null</code>
     * is returned.
     */
    private ArrayList getAncestors(ModelWorkspaceItem element) {
        ModelWorkspaceItem parent = element.getParent();
        if (parent == null) {
            return null;
        }
        ArrayList parents = new ArrayList();
        while (!parent.equals(fChangedElement)) {
            parents.add(parent);
            parent = parent.getParent();
            if (parent == null) {
                return null;
            }
        }
        parents.trimToSize();
        return parents;
    }

    /**
     * Mark this delta as a fine-grained delta.
     */
    public void fineGrained() {
        if (fKind == 0) { // if not set yet
            fKind = CHANGED;
        }
//        fChangeFlags |= F_FINE_GRAINED;
    }

    /**
     * Removes the element from the array.
     * Returns the a new array which has shrunk.
     */
    protected ModelWorkspaceDelta[] removeAndShrinkArray( final ModelWorkspaceDelta[] old, final int index) {
        ModelWorkspaceDelta[] array = new ModelWorkspaceDelta[old.length - 1];
        if (index > 0)
            System.arraycopy(old, 0, array, 0, index);
        int rest = old.length - index - 1;
        if (rest > 0)
            System.arraycopy(old, index + 1, array, index, rest);
        return array;
    }

    /**
     * Adds the new element to a new array that contains all of the elements of the old array.
     * Returns the new array.
     */
    protected ModelWorkspaceDelta[] growAndAddToArray( ModelWorkspaceDelta[] array, final ModelWorkspaceDelta addition) {
        ModelWorkspaceDelta[] old = array;
        array = new ModelWorkspaceDelta[old.length + 1];
        System.arraycopy(old, 0, array, 0, old.length);
        array[old.length] = addition;
        return array;
    }

    /**
     * Adds the child delta to the collection of affected children.  If the
     * child is already in the collection, walk down the hierarchy.
     */
    protected void addAffectedChild(ModelWorkspaceDeltaImpl child) {
        switch (fKind) {
            case ADDED:
            case REMOVED:
                // no need to add a child if this parent is added or removed
                return;
            case CHANGED:
                fChangeFlags |= F_CHILDREN;
                break;
            default:
                fKind = CHANGED;
                fChangeFlags |= F_CHILDREN;
        }

        // if a child delta is added to a compilation unit delta or below, 
        // it's a fine grained delta
        if (fChangedElement.getItemType() >= ModelWorkspaceItem.MODEL_RESOURCE) {
            this.fineGrained();
        }
    
        if (fAffectedChildren.length == 0) {
            fAffectedChildren = new ModelWorkspaceDelta[] {child};
            return;
        }
        ModelWorkspaceDelta existingChild = null;
        int existingChildIndex = -1;
        if (fAffectedChildren != null) {
            for (int i = 0; i < fAffectedChildren.length; i++) {
                if (this.equalsAndSameParent(fAffectedChildren[i].getElement(), child.getElement())) { // handle case of two jars that can be equals but not in the same project
                    existingChild = fAffectedChildren[i];
                    existingChildIndex = i;
                    break;
                }
            }
        }
        if (existingChild == null) { //new affected child
            fAffectedChildren= growAndAddToArray(fAffectedChildren, child);
        } else {
            switch (existingChild.getKind()) {
                case ADDED:
                    switch (child.getKind()) {
                        case ADDED: // child was added then added -> it is added
                        case CHANGED: // child was added then changed -> it is added
                            return;
                        case REMOVED: // child was added then removed -> noop
                            fAffectedChildren = this.removeAndShrinkArray(fAffectedChildren, existingChildIndex);
                            return;
                    }
                    break;
                case REMOVED:
                    switch (child.getKind()) {
                        case ADDED: // child was removed then added -> it is changed
                            child.fKind = CHANGED;
                            fAffectedChildren[existingChildIndex] = child;
                            return;
                        case CHANGED: // child was removed then changed -> it is removed
                        case REMOVED: // child was removed then removed -> it is removed
                            return;
                    }
                    break;
                case CHANGED:
                    switch (child.getKind()) {
                        case ADDED: // child was changed then added -> it is added
                        case REMOVED: // child was changed then removed -> it is removed
                            fAffectedChildren[existingChildIndex] = child;
                            return;
                        case CHANGED: // child was changed then changed -> it is changed
                            ModelWorkspaceDelta[] children = child.getAffectedChildren();
                            for (int i = 0; i < children.length; i++) {
                                ModelWorkspaceDeltaImpl childsChild = (ModelWorkspaceDeltaImpl) children[i];
                                ((ModelWorkspaceDeltaImpl) existingChild).addAffectedChild(childsChild);
                            }
                        
                            // update flags if needed
//                            switch (((ModelWorkspaceDeltaImpl) existingChild).fChangeFlags) {
//                                case F_ADDED_TO_CLASSPATH:
//                                case F_REMOVED_FROM_CLASSPATH:
//                                case F_SOURCEATTACHED:
//                                case F_SOURCEDETACHED:
//                                    ((ModelWorkspaceDeltaImpl) existingChild).fChangeFlags |= ((ModelWorkspaceDeltaImpl) child).fChangeFlags;
//                                    break;
//                            }
                        
                            // add the non-java resource deltas if needed
                            // note that the child delta always takes precedence over this existing child delta
                            // as non-java resource deltas are always created last (by the DeltaProcessor)
                            IResourceDelta[] resDeltas = child.getResourceDeltas();
                            if (resDeltas != null) {
                                ((ModelWorkspaceDeltaImpl)existingChild).resourceDeltas = resDeltas;
                                ((ModelWorkspaceDeltaImpl)existingChild).resourceDeltasCounter = child.resourceDeltasCounter;
                            }
                            return;
                    }
                    break;
                default: 
                    // unknown -> existing child becomes the child with the existing child's flags
                    int flags = existingChild.getFlags();
                    fAffectedChildren[existingChildIndex] = child;
                    child.fChangeFlags |= flags;
            }
        }
    }
    /**
     * Creates the nested deltas resulting from an add operation.
     * Convenience method for creating add deltas.
     * The constructor should be used to create the root delta 
     * and then an add operation should call this method.
     */
    public void added( final ModelWorkspaceItem element) {
        ModelWorkspaceDeltaImpl addedDelta = new ModelWorkspaceDeltaImpl(element);
        addedDelta.fKind = ADDED;
        insertDeltaTree(element, addedDelta);
    }

    /**
     * Creates the nested deltas resulting from a change operation.
     * Convenience method for creating change deltas.
     * The constructor should be used to create the root delta 
     * and then a change operation should call this method.
     */
    public void changed( final ModelWorkspaceItem element, int changeFlag) {
        ModelWorkspaceDeltaImpl changedDelta = new ModelWorkspaceDeltaImpl(element);
        changedDelta.fKind = CHANGED;
        changedDelta.fChangeFlags |= changeFlag;
        insertDeltaTree(element, changedDelta);
    }

    /**
     * Returns whether the two model workspace items are equals and have the same parent.
     */
    protected boolean equalsAndSameParent( final ModelWorkspaceItem e1, final ModelWorkspaceItem e2) {
        ModelWorkspaceItem parent1;
        return e1.equals(e2) && ((parent1 = e1.getParent()) != null) && parent1.equals(e2.getParent());
    }

    /**
     * Returns the ModelWorkspaceDelta for the given element
     * in the delta tree, or null, if no delta for the given element is found.
     */
    protected ModelWorkspaceDeltaImpl find( final ModelWorkspaceItem e) {
        if (this.equalsAndSameParent(fChangedElement, e)) { // handle case of two jars that can be equals but not in the same project
            return this;
        }
        for (int i = 0; i < fAffectedChildren.length; i++) {
            ModelWorkspaceDeltaImpl delta = ((ModelWorkspaceDeltaImpl)fAffectedChildren[i]).find(e);
            if (delta != null) {
                return delta;
            }
        }
        return null;
    }
}
