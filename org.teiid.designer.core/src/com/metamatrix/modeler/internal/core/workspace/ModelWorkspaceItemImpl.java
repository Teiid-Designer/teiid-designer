/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.util.ArrayList;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.HashCodeUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelStatusConstants;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceVisitor;
import com.metamatrix.modeler.core.workspace.Openable;

/**
 * Root of Model Workspace item handle hierarchy.
 * 
 * @see ModelWorkspaceItem
 */
public abstract class ModelWorkspaceItemImpl extends PlatformObject implements ModelWorkspaceItem {

    protected static final int MINIMUM_VALID_TYPE = MODEL_WORKSPACE;
    protected static final int MAXIMUM_VALID_TYPE = MAPPING_CLASS_SETS;

    /**
     * A count to uniquely identify this item in the case that a duplicate named element exists. The occurrence count starts at 1
     * (thus the first occurrence is occurrence 1, not occurrence 0).
     */
    protected int occurrenceCount = 1;

    /**
     * State boolean that describes whether this item is currently opening.
     */
    protected boolean opening = false;

    /**
     * State boolean that describes whether this item is currently closing.
     */
    protected boolean closing = false;

    /**
     * This item's type - one of the constants defined in ModelWorkspaceItem.
     */
    protected final int fType;

    /**
     * This item's parent, or <code>null</code> if this item does not have a parent.
     */
    protected final ModelWorkspaceItemImpl fParent;

    /**
     * This item's name, or an empty <code>String</code> if this item does not have a name.
     */
    protected final String fName;

    protected static final Object NO_INFO = new Object();

    /**
     * Constructs a handle for a model workspace item of the specified type, with the given parent item and name.
     * 
     * @param type - one of the constants defined in ModelWorkspaceItem
     * @exception IllegalArgumentException if the type is not one of the valid model workspace item type constants
     */
    protected ModelWorkspaceItemImpl( final int type,
                                      final ModelWorkspaceItem parent,
                                      final String name ) throws IllegalArgumentException {
        if (type < MINIMUM_VALID_TYPE || type > MAXIMUM_VALID_TYPE) {
            throw new IllegalArgumentException(ModelerCore.Util.getString("element.invalidType")); //$NON-NLS-1$
        }

        // ArgCheck.isNotNull(name); // Should be done in subclasses, not here
        // ArgCheck.isNotZeroLength(name); // Should be done in subclasses, not here
        fType = type;
        fParent = (ModelWorkspaceItemImpl)parent;
        fName = name;
        if (fParent != null) {
            try {
                ModelWorkspaceItemInfo info = (ModelWorkspaceItemInfo)fParent.getItemInfo();
                info.addChild(this);
            } catch (ModelWorkspaceException e) {
                ModelerCore.Util.log(IStatus.ERROR,
                                     e,
                                     ModelerCore.Util.getString("ModelWorkspaceItemImpl.Error_trying_to_create_a_modelWorksapceItem_{0}_under_the_parent_{1}_1", name, fParent.getItemName())); //$NON-NLS-1$
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public String getItemName() {
        return fName;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public int getItemType() {
        return fType;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public ModelWorkspace getModelWorkspace() {
        ModelWorkspaceItem current = this;
        do {
            if (current instanceof ModelWorkspace) return (ModelWorkspace)current;
        } while ((current = current.getParent()) != null);
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public ModelProject getModelProject() {
        ModelWorkspaceItem current = this;
        do {
            if (current instanceof ModelProject) return (ModelProject)current;
        } while ((current = current.getParent()) != null);
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    @SuppressWarnings( "unused" )
    public IResource getCorrespondingResource() throws ModelWorkspaceException {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public Openable getOpenable() {
        return this.getOpenableParent();
    }

    /**
     * Return the first instance of IOpenable in the parent hierarchy of this element.
     * <p>
     * Subclasses that are not IOpenable's must override this method.
     */
    public Openable getOpenableParent() {
        return (Openable)fParent;
    }

    /**
     * Returns the hash code for this model workspace item. By default, the hash code for an element is a combination of its name
     * and parent's hash code. Elements with other requirements must override this method.
     */
    @Override
    public int hashCode() {
        if (fParent == null) {
            return super.hashCode();
        }
        return HashCodeUtil.hashCode(fParent.hashCode(), fName.hashCode());
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * Returns true if this element is an ancestor of the given element, otherwise false.
     * 
     * @param item the item that may be a decendent of this object
     * @return true if this item is an ancestor of the supplied item, or false otherwise (including if <code>item</code> is null)
     */
    protected boolean isAncestorOf( ModelWorkspaceItem item ) {
        if (item == null) {
            return false;
        }
        ModelWorkspaceItem parent = item.getParent();
        while (parent != null && !parent.equals(this)) {
            parent = parent.getParent();
        }
        return parent != null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public ModelWorkspaceItem getParent() {
        return fParent;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public ModelWorkspaceItem[] getChildren() throws ModelWorkspaceException {
        return ((ModelWorkspaceItemInfo)getItemInfo()).getChildren();
    }

    /**
     * Returns a collection of (immediate) children of this node of the specified type.
     * 
     * @param type - one of constants defined by ModelWorkspaceItem
     */
    public ArrayList getChildrenOfType( int type ) throws ModelWorkspaceException {
        ModelWorkspaceItem[] children = getChildren();
        int size = children.length;
        ArrayList list = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            ModelWorkspaceItem elt = children[i];
            if (elt.getItemType() == type) {
                list.add(elt);
            }
        }
        return list;
    }

    /**
     * Returns the first child (immediate) child of this node that has the specified type.
     * 
     * @param type - one of constants defined by ModelWorkspaceItem
     * @return the first child found that has the supplied type, or null if there are no children of that type.
     */
    public ModelWorkspaceItem getFirstChildrenOfType( int type ) throws ModelWorkspaceException {
        ModelWorkspaceItem[] children = getChildren();
        int size = children.length;
        for (int i = 0; i < size; ++i) {
            ModelWorkspaceItem elt = children[i];
            if (elt.getItemType() == type) {
                return elt;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public boolean isStructureKnown() throws ModelWorkspaceException {
        return ((ModelWorkspaceItemInfo)getItemInfo()).isStructureKnown();
    }

    // /**
    // * @see com.metamatrix.modeler.core.ModelWorkspaceItem
    // */
    // public Object getItemInfo() throws ModelWorkspaceException {
    // return getItemInfo(false);
    // }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    public Object getItemInfo() throws ModelWorkspaceException {

        // element info creation is done inside a lock on the ModelWorkspaceManager
        ModelWorkspaceManager manager;
        synchronized (manager = ModelWorkspaceManager.getModelWorkspaceManager()) {
            Object info = manager.getInfo(this);
            if (info == null) {
                openHierarchy();
                info = manager.getInfo(this);
                if (info == null) {
                    throw newNotPresentException();
                }
            }
            return info;
        }
    }

    /**
     * Opens this item and all parents that are not already open.
     * 
     * @exception ModelWorkspaceException this item is not present or accessible
     */
    protected void openHierarchy() throws ModelWorkspaceException {
        if (this instanceof InternalOpenable) {
            ((InternalOpenable)this).openWhenClosed(null);
        } else {
            Openable openableParent = getOpenableParent();
            if (openableParent != null) {
                ModelWorkspaceItemInfo openableParentInfo = (ModelWorkspaceItemInfo)ModelWorkspaceManager.getModelWorkspaceManager().getInfo((ModelWorkspaceItem)openableParent);
                if (openableParentInfo == null) {
                    if (openableParent instanceof InternalOpenable) {
                        ((InternalOpenable)openableParent).openWhenClosed(null);
                    }
                } else {
                    throw newNotPresentException();
                }
            }
        }
    }

    // /**
    // * Opens this item and all parents that are not already open.
    // *
    // * @exception ModelWorkspaceException this item is not present or accessible
    // */
    // protected void openHierarchy() throws ModelWorkspaceException {
    // openHierarchy(false);
    // }

    /**
     * This element has just been opened. Do any necessary setup.
     */
    protected void opening( Object info ) {
    }

    /**
     * @see IOpenable
     */
    public void close() throws ModelWorkspaceException {
        this.closing = true;
        boolean wasVerbose = ModelWorkspaceManager.VERBOSE;
        try {
            Object info = ModelWorkspaceManager.getModelWorkspaceManager().peekAtInfo(this);
            if (info != null) {
                if (ModelWorkspaceManager.VERBOSE) {
                    System.out.println("CLOSING Element (" + Thread.currentThread() + "): " + this.toStringWithAncestors()); //$NON-NLS-1$//$NON-NLS-2$
                    wasVerbose = true;
                    ModelWorkspaceManager.VERBOSE = false;
                }
                ModelWorkspaceItem[] children = ((ModelWorkspaceItemInfo)info).getChildren();
                for (int i = 0, size = children.length; i < size; ++i) {
                    ModelWorkspaceItemImpl child = (ModelWorkspaceItemImpl)children[i];
                    child.close();
                }
                closing(info);
                ModelWorkspaceManager.getModelWorkspaceManager().removeInfo(this);
                if (wasVerbose) {
                    System.out.println("-> Package cache size = " + ModelWorkspaceManager.getModelWorkspaceManager().cache.pkgSize()); //$NON-NLS-1$
                }
            }
        } catch (ModelWorkspaceException e) {
            throw e;
        } catch (Throwable t) {
            ModelerCore.Util.log(t);
        } finally {
            ModelWorkspaceManager.VERBOSE = wasVerbose;
            this.closing = false;
        }
    }

    /**
     * This element is being closed. Do any necessary cleanup.
     */
    protected void closing( Object info ) {
    }

    /**
     * Removes all cached info from the Model Workspace, including all children, but does not close this element.
     */
    protected void removeInfo() {
        Object info = ModelWorkspaceManager.getModelWorkspaceManager().peekAtInfo(this);
        if (info != null) {
            ModelWorkspaceItem[] children = ((ModelWorkspaceItemInfo)info).getChildren();
            for (int i = 0, size = children.length; i < size; ++i) {
                ModelWorkspaceItemImpl child = (ModelWorkspaceItemImpl)children[i];
                child.removeInfo();
            }
            ModelWorkspaceManager.getModelWorkspaceManager().removeInfo(this);
        }
    }

    /**
     * Returns true if this handle represents the same model workspace item as the given handle. By default, two handles represent
     * the same element if they are identical or if they represent the same type of element, have equal names, parents, and
     * occurrence counts.
     * <p>
     * If a subclass has other requirements for equality, this method must be overridden.
     * 
     * @see Object#equals
     */
    @Override
    public boolean equals( Object o ) {

        if (this == o) return true;

        // Model parent is null
        if (fParent == null) return super.equals(o);

        if (o instanceof ModelWorkspaceItemImpl) {
            ModelWorkspaceItemImpl other = (ModelWorkspaceItemImpl)o;
            if (fType != other.fType) return false;

            return fName.equals(other.fName) && fParent.equals(other.fParent) && occurrenceCount == other.occurrenceCount;
        }
        return false;
    }

    /**
     * @see ModelWorkspaceItem
     */
    public boolean exists() {

        try {
            getItemInfo(); // throws exception if cannot obtain info
            return true;
        } catch (ModelWorkspaceException e) {
        }
        return false;
    }

    /**
     * Creates and returns and not present exception for this element.
     */
    protected ModelWorkspaceException newNotPresentException() {
        return new ModelWorkspaceException(new ModelStatusImpl(ModelStatusConstants.ITEM_DOES_NOT_EXIST, this));
    }

    /**
     * Sets the occurrence count of the handle.
     */
    protected void setOccurrenceCount( int count ) {
        occurrenceCount = count;
    }

    protected String tabString( int tab ) {
        StringBuffer buffer = new StringBuffer();
        for (int i = tab; i > 0; i--)
            buffer.append("  "); //$NON-NLS-1$
        return buffer.toString();
    }

    /**
     * Debugging purposes
     */
    public String toDebugString() {
        StringBuffer buffer = new StringBuffer();
        this.toStringInfo(0, buffer, NO_INFO);
        return buffer.toString();
    }

    /**
     * Debugging purposes
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        toString(0, buffer);
        return buffer.toString();
    }

    /**
     * Debugging purposes
     */
    protected void toString( int tab,
                             StringBuffer buffer ) {
        Object info = this.toStringInfo(tab, buffer);
        if (tab == 0) {
            this.toStringAncestors(buffer);
        }
        this.toStringChildren(tab, buffer, info);
    }

    /**
     * Debugging purposes
     */
    public String toStringWithAncestors() {
        StringBuffer buffer = new StringBuffer();
        this.toStringInfo(0, buffer, NO_INFO);
        this.toStringAncestors(buffer);
        return buffer.toString();
    }

    /**
     * Debugging purposes
     */
    protected void toStringAncestors( StringBuffer buffer ) {
        ModelWorkspaceItemImpl parent = (ModelWorkspaceItemImpl)this.getParent();
        if (parent != null && parent.getParent() != null) {
            buffer.append(" [in "); //$NON-NLS-1$
            parent.toStringInfo(0, buffer, NO_INFO);
            parent.toStringAncestors(buffer);
            buffer.append("]"); //$NON-NLS-1$
        }
    }

    /**
     * Debugging purposes
     */
    protected void toStringChildren( int tab,
                                     StringBuffer buffer,
                                     Object info ) {
        if (info == null || !(info instanceof ModelWorkspaceItemInfo)) return;
        ModelWorkspaceItem[] children = ((ModelWorkspaceItemInfo)info).getChildren();
        for (int i = 0; i < children.length; i++) {
            buffer.append("\n"); //$NON-NLS-1$
            ((ModelWorkspaceItemImpl)children[i]).toString(tab + 1, buffer);
        }
    }

    /**
     * Debugging purposes
     */
    public Object toStringInfo( int tab,
                                StringBuffer buffer ) {
        Object info = ModelWorkspaceManager.getModelWorkspaceManager().peekAtInfo(this);
        this.toStringInfo(tab, buffer, info);
        return info;
    }

    /**
     * Debugging purposes
     */
    protected void toStringInfo( int tab,
                                 StringBuffer buffer,
                                 Object info ) {
        buffer.append(this.tabString(tab));
        buffer.append(getItemName());
        if (info == null) {
            buffer.append(" (not open)"); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#accept(com.metamatrix.modeler.core.workspace.ModelWorkspaceVisitor, int)
     */
    public void accept( ModelWorkspaceVisitor visitor,
                        int depth ) throws ModelWorkspaceException {
        ArgCheck.isNotNull(visitor);
        if (depth != DEPTH_INFINITE && depth != DEPTH_ONE && depth != DEPTH_ZERO) {
            throw new IllegalArgumentException(ModelerCore.Util.getString("ModelWorkspaceItemImpl.Invalid_visitor_depth")); //$NON-NLS-1$            
        }

        // visit this resource
        if (!visitor.visit(this) || depth == DEPTH_ZERO) return;

        // visit the children
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO);
        final ModelWorkspaceItem[] children = this.getChildren();
        for (int i = 0; i < children.length; ++i) {
            children[i].accept(visitor, nextDepth);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getChild(org.eclipse.core.resources.IResource)
     */
    public ModelWorkspaceItem getChild( final IResource resource ) throws ModelWorkspaceException {
        ArgCheck.isNotNull(resource);
        final String resourceName = resource.getName();
        return getChild(resourceName);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getChild(java.lang.String)
     */
    public ModelWorkspaceItem getChild( final String childName ) throws ModelWorkspaceException {
        ArgCheck.isNotNull(childName);
        final ModelWorkspaceItem[] children = this.getChildren();
        for (int j = 0; j < children.length; ++j) {
            final ModelWorkspaceItem child = children[j];
            if (child.getItemName().equals(childName)) {
                return child;
            }
        }
        return null;
    }

    public boolean isClosing() {
        return this.closing;
    }

    public boolean isOpening() {
        return this.opening;
    }

}
