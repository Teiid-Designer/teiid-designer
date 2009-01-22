/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.selection;

import java.util.Iterator;
import java.util.List;

import com.metamatrix.core.util.ArgCheck;

/**
 * TreeSelectionImpl
 */
public class TreeSelectionImpl implements TreeSelectionModel {

    private final TreeSelectionState state;
    private final TreeSelectionProvider provider;

    /**
     * Construct an instance of TreeSelectionImpl.
     * 
     */
    public TreeSelectionImpl( final TreeSelectionProvider provider ) {
        super();
        ArgCheck.isNotNull(provider);
        this.provider = provider;
        this.state = new TreeSelectionState(this.provider);
    }
    
    public TreeSelectionProvider getTreeSelectionProvider() {
        return this.provider;
    }

    /**
     * @see com.metamatrix.core.selection.TreeSelection#getSelectionMode(java.lang.Object)
     */
    public int getSelectionMode(final Object node) {
        return this.state.getSelectionMode(node);
    }

    /**
     * @see com.metamatrix.core.selection.TreeSelection#setSelected(java.lang.Object, boolean)
     */
    public void setSelected(final Object node, final boolean selected) {
        final int currentMode = this.state.getSelectionMode(node);
        final int newMode = ( selected ? SELECTED : UNSELECTED );
        if ( currentMode == newMode ) {
            // The value is the same, so simply return
            return;
        }
        
        // Set the current mode ...
        doSetSelectionMode(node,newMode);

        //-------------------------------------------------------------------------
        //                   Update the children ...
        //-------------------------------------------------------------------------

        // If the children haven't been loaded, simply return ...
        final List children = this.provider.getChildren(node);
        if ( children != null ) {

            // If unselecting ...
            if ( newMode == UNSELECTED ) {
                // Go through all the children and unselect them ...
                final Iterator iter = children.iterator();
                while (iter.hasNext()) {
                    final Object child = iter.next();
                    this.setSelected(child,false);
                }
            } else { // if ( this.selectionMode == SELECTED )
                // Go through all the children and select them ...
                final Iterator iter = children.iterator();
                while (iter.hasNext()) {
                    final Object child = iter.next();
                    this.setSelected(child,true);
                }
            }
        }
        
        //-------------------------------------------------------------------------
        //                   Update the parent ...
        //-------------------------------------------------------------------------
        // This may cause the parent (or its ancestors) to each evaluate all of their children
        
        // If there is a parent ...
        final Object parent = this.provider.getParent(node);
        if ( parent != null ) {
            doCheckSelectionMode(parent,node,newMode);
        }
    }

    protected void doSetSelectionMode( final Object node, final int mode ) {
        this.state.setSelected(node,mode);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.InternalJdbcNode#checkSelectionMode(com.metamatrix.modeler.jdbc.metadata.JdbcNode)
     */
    protected void doCheckSelectionMode( final Object parent, 
                                         final Object childNodeWithChangedSelection, final int newMode ) {
        final int parentMode = this.state.getSelectionMode(parent);

        // If the current mode is that of the child ...
        if ( parentMode == newMode ) {
            return;
        }
        
        // Otherwise, we always have to evaluate all children!
        
        // If the children haven't been loaded, then do nothing
        final List children = this.provider.getChildren(parent);
        if ( children == null ) {  // pathological case that should theoretically never happen
            return;
        }
        
        // Go through all the children and see what their mode is ...
        int newParentMode = parentMode;
        boolean hasUnselected = false;
        boolean hasSelected = false;
        boolean hasPartiallySelected = false;
        final Iterator iter = children.iterator();
        while (iter.hasNext()) {
            final Object child = iter.next();
            final int childMode = this.state.getSelectionMode(child);
            if ( !hasSelected && childMode == SELECTED ) {
                hasSelected = true;
            }
            if ( !hasUnselected && childMode == UNSELECTED ) {
                hasUnselected = true;
            }
            if ( !hasPartiallySelected && childMode == PARTIALLY_SELECTED ) {
                hasPartiallySelected = true;
            }
            
            // See if we know enough to set this node ...
            if ( hasPartiallySelected || (hasSelected && hasUnselected) ) {
                // A child is partially selected, or there are both selected & unselected children ...
                hasPartiallySelected = true;
                doSetSelectionMode(parent,PARTIALLY_SELECTED);
                newParentMode = PARTIALLY_SELECTED;
                break;
            }
        }

        // We're through all the children, so they are all either SELECTED or UNSELECTED
        if ( !hasPartiallySelected ) {
            if ( hasSelected ) {
                doSetSelectionMode(parent,SELECTED);
                newParentMode = SELECTED;
            }
            if ( hasUnselected ) {
                doSetSelectionMode(parent,UNSELECTED);
                newParentMode = UNSELECTED;
            }
        }
        
        // If the value changed ...
        if ( newParentMode != parentMode ) {
            // call this method on the parent ...
            final Object parentParent = this.provider.getParent(parent);
            if ( parentParent != null) {
                doCheckSelectionMode(parentParent,parent,newParentMode);
            }
        }
    }
    
}
