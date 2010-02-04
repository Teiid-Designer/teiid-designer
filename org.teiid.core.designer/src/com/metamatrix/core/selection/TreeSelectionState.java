/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.selection;

import java.util.HashSet;
import java.util.Set;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * TreeSelectionState
 */
public class TreeSelectionState {

    public static final int SELECTED = TreeSelection.SELECTED;
    
    public static final int UNSELECTED = TreeSelection.UNSELECTED;
    
    public static final int PARTIALLY_SELECTED = TreeSelection.PARTIALLY_SELECTED;
    
    public static final int UNKNOWN = -1;
    
    private final Set selecteds;
    private final Set unselecteds;
    private final Set partiallySelected;
    private final TreeSelectionProvider provider;

    /**
     * Construct an instance of TreeSelectionState.
     * 
     */
    public TreeSelectionState( final TreeSelectionProvider provider ) {
        super();
        ArgCheck.isNotNull(provider);
        this.selecteds = new HashSet();
        this.unselecteds = new HashSet();
        this.partiallySelected = new HashSet();
        this.provider = provider;
    }

    /**
     * Return whether there are any paths that are known to be selected, unselected or partially selected.
     * @return true if there are at least some paths known to be selected, unselected or partially selected,
     * or false if there no known selection modes for any path
     */
    public boolean hasSelectionModes() {
        return this.selecteds.size() != 0 || this.partiallySelected.size() != 0 || this.unselecteds.size() != 0;
    }

    /**
     * Return the selection mode on this the specified object.
     * @param modelObject the object for which the selection state is to be returned; may not be null
     * @return the current selection; one of {@link #SELECTED}, {@link #PARTIALLY_SELECTED}
     * or {@link #UNSELECTED} 
     */
    public int getSelectionMode( final Object node ) {
         ArgCheck.isNotNull(node);
        if ( this.selecteds.contains(node) ) {
            return SELECTED;
        }
        if ( this.unselecteds.contains(node) ) {
            return UNSELECTED;
        }
        if ( this.partiallySelected.contains(node) ) {
            return PARTIALLY_SELECTED;
        }
        
        // -------------------------------------------------------------------
        // The path is not known.  However, the "default" can be determined by
        // looking at the ancestors.
        // -------------------------------------------------------------------
        
        // If this path is for a root object ...
        if ( this.provider.isRoot(node) ) {
            // so the default should be to be UNSELECTED
            this.unselecteds.add(node);
            return UNSELECTED;
        }
        
        // Get the parent path and see what it's selection mode is ...
        final Object parentKey = this.provider.getParent(node);
        final int parentMode = getSelectionMode(parentKey);    // recursive!!!
        if ( parentMode == SELECTED ) {
            // The parent is fully selected, so should this node ...
            this.selecteds.add(node);
            return SELECTED;
        }
        if ( parentMode == PARTIALLY_SELECTED ) {
            // The parent is partially selected, so we don't know what to assume 
            return UNKNOWN;
//            // The parent is partially selected, so we'll assume nodes underneath a partially-selected
//            // node should be fully selected
//            this.selecteds.add(path);
//            return SELECTED;
        }
        // Parent is unselected, so this node should be as well ...
        return UNSELECTED;
    }
    
    public void setSelected( final Object node, final int selectionMode ) {
        ArgCheck.isNotNull(node);
        if ( selectionMode == SELECTED ) {
            this.selecteds.add(node);
            this.unselecteds.remove(node);
            this.partiallySelected.remove(node);
        } else if ( selectionMode == UNSELECTED ) {
            this.selecteds.remove(node);
            this.unselecteds.add(node);
            this.partiallySelected.remove(node);
        } else if ( selectionMode == PARTIALLY_SELECTED ) {
            this.selecteds.remove(node);
            this.unselecteds.remove(node);
            this.partiallySelected.add(node);
        }
    }
    
}
